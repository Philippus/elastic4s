package com.sksamuel.elastic4s

import scala.util.DynamicVariable
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}

/** @author Stephen Samuel */
case class SearchReq(query: Query,
                     filter: Option[Filter] = None,
                     timeout: Long = 0,
                     from: Long = 0,
                     size: Long = 0,
                     explain: Boolean = false,
                     sort: Option[Sort] = None,
                     routing: Option[String],
                     version: Boolean = false,
                     minScore: Double = 0,
                     fields: Seq[String] = Nil,
                     searchType: SearchType = SearchType.QueryThenFetch) {

    def _source: XContentBuilder = {
        val source = XContentFactory.jsonBuilder().startObject()
        source.endObject()
    }
}
case class SearchResp(hits: Hits, facets: Seq[Facet])

case class Hits(total: Long, hits: Seq[Hit])
case class Hit(index: String, id: String, `type`: String, source: Option[String] = None)

case class Sort
case class Highlight
case class Facet
case class FacetTerm
case class FacetTermValue
case class IndexBoost

trait Filter
trait Query

sealed trait SearchType
case object SearchType {
    case object DfsQueryThenFetch extends SearchType
    case object QueryThenFetch extends SearchType
    case object DfsQueryAndFetch extends SearchType
    case object QueryAndFetch extends SearchType
    case object Scan extends SearchType
    case object Count extends SearchType
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
case class PrefixQuery(field: String, prefix: String, boost: Double = 0) extends Query
case class BoostingQuery(positive: Query, positiveBoost: Double, negative: Query, negativeBoost: Double) extends Query
case class RegexQuery(field: String, regex: String, boost: Double = 0, flags: Seq[Int]) extends Query

trait SearchDsl {

    private val searchBuilderContext = new DynamicVariable[Option[SearchBuilder]](None)
    private val queryContext = new DynamicVariable[Option[QueryBuilderContainer]](None)

    def search(index: String, `type`: String)(block: => Unit): SearchReq = {
        val builder = new SearchBuilder(Seq(index), Seq(`type`))
        searchBuilderContext.withValue(Some(builder)) {
            block
        }
        builder.build
    }

    def routing(r: String) {
        searchBuilderContext.value foreach (_._routing = Option(r))
    }

    def from(i: Int) {
        searchBuilderContext.value foreach (_._from = i)
    }

    def size(i: Int) {
        searchBuilderContext.value foreach (_._size = i)
    }

    def searchType(searchType: SearchType) {
        searchBuilderContext.value foreach (_._searchType = searchType)
    }

    def version(enabled: Boolean) {
        searchBuilderContext.value foreach (_._version = enabled)
    }

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
        val container = new BoolQueryContainer
        queryContext.withValue(Some(container)) {
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

    def _setQuery(builder: QueryBuilder) {
        queryContext.value match {
            case None => searchBuilderContext.value foreach (_._queryBuilder = Option(builder))
            case Some(q) => q.attach(builder)
        }
    }
}

trait QueryBuilderContainer {
    def attach(builder: QueryBuilder)
}

class BoolQueryContainer extends QueryBuilderContainer {
    def attach(builder: QueryBuilder) {

    }
}

class BoolQueryBuilder extends BoostableQueryBuilder with QueryBuilder {

    var minimum_number_should_match = 1

    def must(block: => Unit) {

    }

    def mustNot(block: => Unit) {

    }

    def should(block: => Unit) {

    }

    def build = null
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
    override def build = PrefixQuery(field, value.toString, _boost)
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
