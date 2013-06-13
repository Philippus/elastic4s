package com.sksamuel.elastic4s

import scala.util.DynamicVariable
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}
import com.sksamuel.elastic4s.MultiMode.Min
import scala.collection.mutable.ListBuffer
import org.elasticsearch.search.facet.FacetBuilders

/** @author Stephen Samuel */
case class SearchReq(indexes: Seq[String],
                     query: Query,
                     filter: Option[Filter] = None,
                     facets: Seq[Facet] = Nil,
                     fields: Seq[String] = Nil,
                     timeout: Long = 0,
                     from: Long = 0,
                     size: Long = 0,
                     lowercaseExpandedTerms: Boolean = true,
                     trackScores: Boolean = false,
                     scroll: Option[String] = None,
                     scrollId: Option[String] = None,
                     explain: Boolean = false,
                     sorts: Seq[Sort] = Nil,
                     routing: Seq[String] = Nil,
                     version: Boolean = false,
                     minScore: Double = 0,
                     highlight: Option[Highlight] = None,
                     searchType: SearchType = SearchType.QueryThenFetch) {

    def _source: XContentBuilder = {
        val source = XContentFactory.jsonBuilder().startObject()
        source.endObject()
    }
}

case class SearchResp(hits: Hits, facets: Seq[Facet])

case class Hits(total: Long, hits: Seq[Hit])
case class Hit(index: String, id: String, `type`: String, source: Option[String] = None)

case class Sort(field: String, asc: Boolean = true, mode: MultiMode = Min)

sealed trait MultiMode
case object MultiMode {
    case object Min extends MultiMode
    case object Max extends MultiMode
    case object Sum extends MultiMode
    case object Avg extends MultiMode
}

case class Highlight(fields: Seq[HighlightField] = Nil,
                     preTags: Seq[String] = Nil,
                     postTags: Seq[String] = Nil)
case class HighlightField(name: String, fragmentSize: Int = 100, numberOfFragments: Int = 5)

trait Filter
trait Query
abstract class Facet(name: String) {
    val global: Boolean = false
    def builder: org.elasticsearch.search.facet.FacetBuilder
}

case class TermsFacet(name: String,
                      fields: Seq[String],
                      order: TermsFacetOrder = TermsFacetOrder.Count,
                      size: Int = 10,
                      allTerms: Boolean = false,
                      excludeTerms: Seq[String] = Nil,
                      script: Option[String] = None,
                      regex: Option[String] = None,
                      regexFlags: Seq[String] = Nil,
                      script_field: Option[String] = None) extends Facet(name) {
    def builder = FacetBuilders
      .termsFacet(name)
      .allTerms(allTerms)
      .order(order.elasticType)
      .regex(regex.orNull)
      .script(script.orNull)
      .global(global)
      .scriptField(script_field.orNull)
      .fields(fields: _ *)
      .size(size)
}

case class RangeFacet(name: String, field: String, ranges: Seq[Range], keyField: Option[String] = None, valueField: Option[String] = None)
  extends Facet(name) {
    def builder = {
        val builder = FacetBuilders.rangeFacet(name).field(field).keyField(keyField.orNull).valueField(valueField.orNull)
        for ( range <- ranges )
            builder.addRange(range.start, range.end)
        builder
    }
}

case class QueryFacet(name: String, query: Query) extends Facet(name) {
    def builder = FacetBuilders.queryFacet(name, null)
}

case class FilterFacet(name: String, query: Query) extends Facet(name) {
    def builder = FacetBuilders.filterFacet(name, null)
}

abstract class TermsFacetOrder(val elasticType: org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType)
case object TermsFacetOrder {
    case object Count extends TermsFacetOrder(org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType.COUNT)
    case object Term extends TermsFacetOrder(org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType.TERM)
    case object ReverseCount extends TermsFacetOrder(org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType.REVERSE_COUNT)
    case object ReverseTerm extends TermsFacetOrder(org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType.REVERSE_TERM)
}

abstract class SearchType(val elasticType: org.elasticsearch.action.search.SearchType)
case object SearchType {
    case object DfsQueryThenFetch extends SearchType(org.elasticsearch.action.search.SearchType.DFS_QUERY_THEN_FETCH)
    case object QueryThenFetch extends SearchType(org.elasticsearch.action.search.SearchType.QUERY_THEN_FETCH)
    case object DfsQueryAndFetch extends SearchType(org.elasticsearch.action.search.SearchType.QUERY_AND_FETCH)
    case object QueryAndFetch extends SearchType(org.elasticsearch.action.search.SearchType.QUERY_AND_FETCH)
    case object Scan extends SearchType(org.elasticsearch.action.search.SearchType.SCAN)
    case object Count extends SearchType(org.elasticsearch.action.search.SearchType.COUNT)
}

case class StringQuery(queryString: String,
                       defaultField: Option[String] = None,
                       defaultOperator: String = "AND",
                       analyzer: Option[Analyzer] = None,
                       allow_leading_wildcard: Boolean = true,
                       lowercase_expanded_terms: Boolean = true,
                       enable_position_increments: Boolean = true,
                       boost: Double = 0,
                       fuzzy_max_expansions: Int = 50,
                       fuzzy_min_sim: Double = 0.5,
                       fuzzy_prefix_length: Int = 0,
                       phrase_slop: Int = 0,
                       lenient: Boolean = true,
                       minimumShouldMatch: Int = 0,
                       exists: Option[String] = None,
                       missing: Option[String] = None) extends Query

case class MatchQuery(field: String,
                      query: Any,
                      defaultOperator: String = "OR",
                      analyzer: Option[Analyzer] = None,
                      boost: Double = 0,
                      cutoffFrequency: Double = 0,
                      fuzzy_max_expansions: Int = 50,
                      fuzzy_min_sim: Double = 0.5,
                      fuzzy_prefix_length: Int = 0,
                      phrase_slop: Int = 0) extends Query
case class MatchAllQuery(boost: Double = 0) extends Query

case class FieldQuery(field: String, value: String, boost: Double = 0, enablePositionIncrements: Boolean) extends Query
case class TermQuery(field: String, value: String, boost: Double = 0) extends Query
case class RangeQuery(field: String, from: Any, to: Any, includeUpper: Boolean = true, includeLower: Boolean = true, boost: Double = 0)
  extends Query
case class TermsQuery(field: String, values: Seq[String], boost: Double = 0) extends Query
case class BoolQuery(must: Seq[Any], mustNot: Seq[Any], should: Seq[Any], minimumShouldMatch: Int = 0, boost: Double = 0)
  extends Query
case class PrefixQuery(field: String, prefix: Any, boost: Double = 0) extends Query
case class BoostingQuery(positive: Query, positiveBoost: Double, negative: Query, negativeBoost: Double) extends Query
case class RegexQuery(field: String, regex: Any, boost: Double = 0, flags: Seq[Int] = Nil) extends Query

trait SearchDsl {

    private val searchBuilderContext = new DynamicVariable[Option[SearchBuilder]](None)
    private val queryContext = new DynamicVariable[Option[CompoundQuery]](None)
    private val highlightContext = new DynamicVariable[Option[HighlightBuilder]](None)

    def search(index: String, `type`: String)(block: => Unit): SearchReq = {
        val builder = new SearchBuilder(Seq(index), Seq(`type`))
        searchBuilderContext.withValue(Some(builder)) {
            block
        }
        builder.build
    }

    def routing(r: String): Unit = searchBuilderContext.value foreach (_._routing = Option(r))
    def from(i: Int): Unit = searchBuilderContext.value foreach (_._from = i)
    def size(i: Int): Unit = searchBuilderContext.value foreach (_._size = i)

    def searchType(searchType: SearchType): Unit = searchBuilderContext.value foreach (_._searchType = searchType)
    def version(enabled: Boolean): Unit = searchBuilderContext.value foreach (_._version = enabled)

    def query(q: String): StringQueryBuilder = {
        val builder = new StringQueryBuilder(q)
        _setQuery(builder)
        builder
    }

    def field(field: String, value: Any): StringQueryBuilder = {
        val builder = new StringQueryBuilder(value.toString)
        builder.defaultField(field)
        _setQuery(builder)
        builder
    }

    def prefix(field: String, value: Any): PrefixQueryBuilder = {
        val builder = new PrefixQueryBuilder(field, value)
        _setQuery(builder)
        builder
    }

    def term(field: String, value: Any): TermQueryBuilder = {
        val builder = new TermQueryBuilder(field, value)
        _setQuery(builder)
        builder
    }

    def bool(block: => Unit) {
        val compound = new BoolCompoundQuery
        queryContext.withValue(Some(compound)) {
            block
        }
    }

    def matches(field: String, value: Any) = {
        val builder = new MatchQueryBuilder(field, value)
        _setQuery(builder)
        builder
    }

    def matchAll() = {
        val builder = new MatchAllQueryBuilder()
        _setQuery(builder)
        builder
    }

    // -- highlight methods --

    def highlight(block: => Unit) {
        val builder = new HighlightBuilder()
        highlightContext.withValue(Some(builder)) {
            block
        }
    }

    def highlightField(name: String): HighlightFieldBuilder = {
        val builder = new HighlightFieldBuilder(name)
        highlightContext.value foreach (_._fields.append(builder))
        builder
    }

    def preTags(tag: String): Unit = preTags(Seq(tag))
    def preTags(tags: Seq[String]): Unit = highlightContext.value foreach (_._preTags = tags)

    def postTags(tag: String): Unit = postTags(Seq(tag))
    def postTags(tags: Seq[String]): Unit = highlightContext.value foreach (_._postTags = tags)

    // -- boolean query clauses --

    def must(block: => Unit) {

    }

    def mustNot(block: => Unit) {

    }

    def should(block: => Unit) {

    }

    // -- facet dsl --

    def facets(block: => Unit) {
    }

    def _setQuery(builder: QueryBuilder) {
        queryContext.value match {
            case None => searchBuilderContext.value foreach (_._queryBuilder = Option(builder))
            case Some(q) => q.attach(builder)
        }
    }
}

trait CompoundQuery {
    def attach(builder: QueryBuilder)
}

class BoolCompoundQuery extends CompoundQuery {
    def attach(builder: QueryBuilder) {

    }
}

class BoolQueryBuilder extends BoostableQueryBuilder with QueryBuilder {

    var minimumNumberShouldMatch = 1

    def build = null
}

trait FacetBuilder {
    def build: Facet
}

trait QueryBuilder {
    def build: Query
}
abstract class BoostableQueryBuilder[T] {
    var _boost: Double = 0
    def boost(boost: Double): T = {
        _boost = boost
        this.asInstanceOf[T]
    }
}
class PrefixQueryBuilder(field: String, value: Any) extends BoostableQueryBuilder[PrefixQueryBuilder] with QueryBuilder {
    override def build = PrefixQuery(field, value, _boost)
}
class RegexQueryBuilder(field: String, value: Any) extends BoostableQueryBuilder[RegexQueryBuilder] with QueryBuilder {
    override def build = RegexQuery(field, value, _boost)
}
class TermQueryBuilder(field: String, value: Any) extends BoostableQueryBuilder[TermQueryBuilder] with QueryBuilder {
    override def build = TermQuery(field, value.toString, _boost)
}
class MatchAllQueryBuilder extends BoostableQueryBuilder[MatchAllQueryBuilder] with QueryBuilder {
    override def build = MatchAllQuery(_boost)
}
class StringQueryBuilder(q: String) extends BoostableQueryBuilder[StringQueryBuilder] with QueryBuilder {

    var _defaultOperator: String = "AND"
    var _analyzer: Option[Analyzer] = None
    var _defaultField: Option[String] = None

    def operator(op: String) = {
        _defaultOperator = op
        this
    }

    def defaultField(field: String) = {
        _defaultField = Option(field)
        this
    }

    def analyzer(a: Analyzer) = {
        _analyzer = Option(a)
        this
    }

    override def build = StringQuery(q, defaultOperator = _defaultOperator, analyzer = _analyzer, boost = _boost)
}
class RangeQueryBuilder(field: String, from: Any, to: Any) extends BoostableQueryBuilder[RangeQueryBuilder] with QueryBuilder {
    var _includeLower = true
    var _includeUpper = true

    def includeLower(includeLower: Boolean) = {
        _includeLower = includeLower
        this
    }

    def includeUpper(includeUpper: Boolean) = {
        _includeUpper = includeUpper
        this
    }

    override def build = RangeQuery(field, from, to, _includeLower, _includeUpper, _boost)
}
class MatchQueryBuilder(field: String, value: Any) extends BoostableQueryBuilder[MatchQueryBuilder] with QueryBuilder {

    var _defaultOperator: String = "AND"
    var _analyzer: Option[Analyzer] = None

    def operator(op: String) = {
        _defaultOperator = op
        this
    }

    def analyzer(a: Analyzer) = {
        _analyzer = Option(a)
        this
    }

    override def build = MatchQuery(field, value, defaultOperator = _defaultOperator, analyzer = _analyzer, boost = _boost)
}

class SearchBuilder(indexes: Seq[String], types: Seq[String]) {
    var _routing: Option[String] = None
    var _size = 0
    var _from = 0
    var _version = false
    var _queryBuilder: Option[QueryBuilder] = None
    var _searchType: SearchType = SearchType.DfsQueryThenFetch
    def build: SearchReq = null
}

class HighlightBuilder {

    val _fields = ListBuffer[HighlightFieldBuilder]()
    var _preTags: Seq[String] = Nil
    var _postTags: Seq[String] = Nil

    def build: Highlight = {
        Highlight(_fields.map(_.build), _preTags, _postTags)
    }
}

class HighlightFieldBuilder(name: String) {

    private var _fragmentSize = 100
    private var _numberOfFragments = 5

    def fragmentSize(f: Int) = {
        _fragmentSize = f
        this
    }

    def numberOfFragments(n: Int) = {
        _numberOfFragments = n
        this
    }

    def build: HighlightField = HighlightField(name, fragmentSize = _fragmentSize, numberOfFragments = _numberOfFragments)
}
