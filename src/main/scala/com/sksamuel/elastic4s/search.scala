package com.sksamuel.elastic4s

import scala.util.DynamicVariable
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}
import org.elasticsearch.search.facet.FacetBuilders
import org.elasticsearch.search.highlight.HighlightBuilder
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.index.query.{RegexpFlag, QueryStringQueryBuilder, QueryBuilders}

/** @author Stephen Samuel */
case class SearchReq(indexes: Iterable[String] = Nil,
                     types: Iterable[String] = Nil,
                     query: Option[Query] = None,
                     filter: Option[Filter] = None,
                     facets: Seq[Facet] = Nil,
                     fields: Seq[String] = Nil,
                     from: Long = 0,
                     explain: Boolean = false,
                     highlight: Option[Highlight] = None,
                     lowercaseExpandedTerms: Boolean = true,
                     minScore: Double = 0,
                     queryHint: Option[String] = None,
                     routing: Seq[String] = Nil,
                     size: Long = 0,
                     source: Option[String] = None,
                     scroll: Option[String] = None,
                     scrollId: Option[String] = None,
                     sorts: Seq[Sort] = Nil,
                     timeout: Long = 0,
                     trackScores: Boolean = false,
                     version: Boolean = false,
                     searchType: SearchType = SearchType.QueryThenFetch) {

    def builder: org.elasticsearch.action.search.SearchRequestBuilder = {
        null
    }

    def _source: XContentBuilder = {
        val source = XContentFactory.jsonBuilder().startObject()
        source.endObject()
    }
}

sealed trait MultiMode
case object MultiMode {
    case object Min extends MultiMode
    case object Max extends MultiMode
    case object Sum extends MultiMode
    case object Avg extends MultiMode
}

sealed trait HighlightOrder
case object HighlightOrder {
    case object Score extends HighlightOrder
}

sealed trait TagSchema
case object TagSchema {
    case object Styled extends TagSchema
}

sealed trait HighlightEncoder
case object HighlightEncoder {
    case object Default extends HighlightEncoder
    case object Html extends HighlightEncoder
}

case class Highlight(fields: Iterable[HighlightField] = Nil,
                     preTags: Seq[String] = Nil,
                     postTags: Seq[String] = Nil,
                     encoder: Option[HighlightEncoder] = None,
                     order: Option[HighlightOrder] = None,
                     tagSchema: Option[TagSchema] = None,
                     requireFieldMatch: Boolean = false,
                     boundary_chars: Option[String] = None,
                     boundary_max_scan: Int = 20) {
    def builder: org.elasticsearch.search.highlight.HighlightBuilder = {
        val builder = new org.elasticsearch.search.highlight.HighlightBuilder()
          .postTags(postTags: _*)
          .preTags(preTags: _*)
          .requireFieldMatch(requireFieldMatch)
        encoder.foreach(arg => builder.encoder(arg.toString.toLowerCase))
        order.foreach(arg => builder.order(arg.toString.toLowerCase))
        tagSchema.foreach(arg => builder.tagsSchema(arg.toString.toLowerCase))
        builder
    }
}
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

//case class StringQuery(queryString: String,
//                       defaultField: Option[String] = None,
//                       defaultOperator: String = "AND",
//                       analyzer: Option[Analyzer] = None,
//                       allowLeadingWildcard: Boolean = true,
//                       lowercaseExpandedTerms: Boolean = true,
//                       enablePositionIncrements: Boolean = true,
//                       boost: Double = 0,
//                       fuzzyMaxExpansions: Int = 50,
//                       fuzzyMinSim: Double = 0.5,
//                       fuzzyPrefixLength: Int = 0,
//                       phraseSlop: Int = 0,
//                       lenient: Boolean = true,
//                       minimumShouldMatch: Int = 0,
//                       exists: Option[String] = None,
//                       missing: Option[String] = None) extends Query

class SearchBuilder(indexes: Seq[String]) {

    private val highlightingContext = new DynamicVariable[Option[HighlightBuilder]](None)
    val request = new SearchRequestBuilder(null)
    request.setIndices(indexes: _*)

    def query(block: => Any) = {
        this
    }

    def query(string: String) = {
        val query = new StringQueryBuilder(query)
        request.setQuery(query.builder.buildAsBytes)
        this
    }

    def highlighting(block: => Unit) {
        val context = new HighlightBuilder()
        highlightingContext.withValue(Some(context)) {
            block
        }
    }

    def routing(r: String) = {
        request.setRouting(r)
        this
    }

    def start(i: Int) = from(i)
    def from(i: Int) = {
        request.setFrom(i)
        this
    }

    def limit(i: Int) = size(i)
    def size(i: Int) = {
        request.setSize(i)
        this
    }

    def searchType(searchType: SearchType) = {
        request.setSearchType(searchType.elasticType)
        this
    }

    def version(enabled: Boolean) = {
        request.setVersion(enabled)
        this
    }

    def execute() = {
        null
    }
}

class HighlightFieldBuilderInitial {
    def field(field: String) = new HighlightFieldBuilder(field)
}

class HighlightFieldBuilder(field: String) {

    val builder = new org.elasticsearch.search.highlight.HighlightBuilder.Field(field)

    def size(s: Int) = fragmentSize(s)

    def fragmentSize(f: Int) = {
        builder.fragmentSize(f)
        this
    }

    def number(n: Int) = numberOfFragments(n)
    def numberOfFragments(n: Int) = {
        builder.numOfFragments(n)
        this
    }

    def highlighterType(`type`: String) = {
        builder.highlighterType(`type`)
        this
    }

    def and(field: String) = new HighlightFieldBuilder(field: String)
}

object SearchDsl {

    def preTag(tag: String): Unit = this
    def postTag(tag: String): Unit = this

    def highlight = new HighlightFieldBuilderInitial
    def highlight(field: String) = new HighlightFieldBuilder(field)

    private val searchBuilderContext = new DynamicVariable[Option[SearchBuilder]](None)
    implicit def string2search(index: String) = new SearchBuilder(Seq(index))

    def search = new SearchBuilderExpectsIndex
    class SearchBuilderExpectsIndex {
        def index(index: String) = new SearchBuilder(Seq(index))
        def index(indexes: Seq[String]) = new SearchBuilder(indexes)
    }

    def search2(index: String, `type`: String)(block: => Unit): SearchReq = {
        val builder = null
        searchBuilderContext.withValue(Some(builder)) {
            block
        }
        builder
    }

    implicit class StringQueryHelper(val sc: StringContext) extends AnyVal {
        def q(args: String*): StringQueryBuilder = new StringQueryBuilder(args.head)
    }

    trait PairQuery {
        type QueryBuilder
        def query(q: String): QueryBuilder = _query(q.split(":").toSeq)
        def _query(q: Seq[String]): QueryBuilder = query(q(0), q(1))
        def query(kv: (String, Any)): QueryBuilder = query(kv._1, kv._2)
        def query(field: String, value: Any): QueryBuilder
    }

    def string = new StringExpectsQueryOrFilter
    class StringExpectsQueryOrFilter {
        def query(q: String) = new StringQueryBuilder(q)
    }

    def prefix = new PrefixExpectsQueryOrFilter
    class PrefixExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = PrefixQueryBuilder
        def query(field: String, value: Any): PrefixQueryBuilder = new PrefixQueryBuilder(field, value)
    }

    def term = new TermExpectsQueryOrFilter
    class TermExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = TermQueryBuilder
        def query(field: String, value: Any): TermQueryBuilder = new TermQueryBuilder(field, value)
    }

    def matches = new MatchExpectsQueryOrFilter
    class MatchExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = MatchQueryBuilder
        def query(field: String, value: Any): MatchQueryBuilder = new MatchQueryBuilder(field, value)
    }

    def range = new RangeExpectsQueryOrFilter
    class RangeExpectsQueryOrFilter {
        def query(field: String): RangeQueryBuilder = new RangeQueryBuilder(field)
    }

    def regex = new RegexExpectsQueryOrFilter
    class RegexExpectsQueryOrFilter extends PairQuery {
        def query(field: String, value: Any): RegexQueryBuilder = new RegexQueryBuilder(field, value)
    }

    def term(field: String, value: Any): TermQueryBuilder = {
        val builder = new TermQueryBuilder(field, value)
        builder
    }

    def matchAll() = {
        val builder = new MatchAllQueryBuilder()
        builder
    }
}

trait FacetBuilder {
    def build: Facet
}