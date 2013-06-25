package com.sksamuel.elastic4s

import scala.util.DynamicVariable
import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}
import org.elasticsearch.search.highlight.HighlightBuilder
import org.elasticsearch.action.search.SearchRequestBuilder

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

trait Filter
trait Query
abstract class Facet(name: String) {
    val global: Boolean = false
    def builder: org.elasticsearch.search.facet.FacetBuilder
}

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