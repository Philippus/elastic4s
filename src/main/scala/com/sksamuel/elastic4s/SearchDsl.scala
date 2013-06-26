package com.sksamuel.elastic4s

import org.elasticsearch.action.search.SearchRequestBuilder

/** @author Stephen Samuel */
//case class SearchReq(indexes: Iterable[String] = Nil,
//                     types: Iterable[String] = Nil,
//                     query: Option[Query] = None,
//                     filter: Option[Filter] = None,
//                     facets: Seq[Facet] = Nil,
//                     fields: Seq[String] = Nil,
//                     from: Long = 0,
//                     explain: Boolean = false,
//                     highlight: Option[Highlight] = None,
//                     lowercaseExpandedTerms: Boolean = true,
//                     minScore: Double = 0,
//                     queryHint: Option[String] = None,
//                     routing: Seq[String] = Nil,
//                     size: Long = 0,
//                     source: Option[String] = None,
//                     scroll: Option[String] = None,
//                     scrollId: Option[String] = None,
//                     sorts: Seq[Sort] = Nil,
//                     timeout: Long = 0,
//                     trackScores: Boolean = false,
//                     version: Boolean = false,
//                     searchType: SearchType = SearchType.QueryThenFetch) {
//
//    def builder: org.elasticsearch.action.search.SearchRequestBuilder = {
//        null
//    }
//
//    def _source: XContentBuilder = {
//        val source = XContentFactory.jsonBuilder().startObject()
//        source.endObject()
//    }
//}


object SearchDsl {

    abstract class Facet(name: String) {
        val global: Boolean = false
        def builder: org.elasticsearch.search.facet.FacetBuilder
    }

    def preTag(tag: String): Unit = this
    def postTag(tag: String): Unit = this

    def highlight = new HighlightBuilder
    def highlight(field: String) = new HighlightBuilder

    class SearchBuilder(indexes: Seq[String], types: Seq[String]) {

        val builder = new SearchRequestBuilder(null).setIndices(indexes: _*).setTypes(types: _*)

        def query(block: => Any): SearchBuilder = {
            this
        }

        /**
         * Adds a single string query to this search
         *
         * @param string the query string
         *
         * @return this
         */
        def query(string: String) = {
            val q = new StringQueryBuilder(string)
            builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def prefix(tuple: (String, Any)) = {
            val q = new PrefixQueryBuilder(tuple._1, tuple._2)
            builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def regex(tuple: (String, Any)) = {
            val q = new RegexQueryBuilder(tuple._1, tuple._2)
            builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def term(tuple: (String, Any)) = {
            val q = new TermQueryBuilder(tuple._1, tuple._2)
            builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def range(field: String) = {
            val q = new RangeQueryBuilder(field)
            builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def highlight(field: String) = new HighlightBuilder

        def highlighting(block: => Unit) {

        }

        def routing(r: String) = {
            builder.setRouting(r)
            this
        }

        def start(i: Int) = from(i)
        def from(i: Int) = {
            builder.setFrom(i)
            this
        }

        def limit(i: Int) = size(i)
        def size(i: Int) = {
            builder.setSize(i)
            this
        }

        def searchType(searchType: SearchType) = {
            builder.setSearchType(searchType.elasticType)
            this
        }

        def version(enabled: Boolean) = {
            builder.setVersion(enabled)
            this
        }
    }

    class Highlighter(request: SearchRequestBuilder) extends SearchBuilder(null, null) {
        def fields(any: HighlightBuilder*) = {
            //        val builder = block
            //      builder.buffer.foreach(field => request.addHighlightedField(field))
            this
        }
    }

    implicit def string2highlightField(field: String) = {
        val builder = new HighlightBuilder
        builder.field(field)
        builder
    }

    def search = new SearchExpectsIndex
    class SearchExpectsIndex {
        def in(indexes: String*): IndexExpectsType = new IndexExpectsType(indexes)
    }

    class IndexExpectsType(indexes: Seq[String]) {
        def types(types: String*): SearchBuilder = new SearchBuilder(indexes, types)
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

    def matches = new MatchExpectsQueryOrFilter
    class MatchExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = MatchQueryBuilder
        def query(field: String, value: Any): MatchQueryBuilder = new MatchQueryBuilder(field, value)
    }

    def all = matchAll()
    def matchAll() = {
        val builder = new MatchAllQueryBuilder()
        builder
    }

    def prefix = new PrefixExpectsQueryOrFilter
    class PrefixExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = PrefixQueryBuilder
        def query(field: String, value: Any): PrefixQueryBuilder = new PrefixQueryBuilder(field, value)
    }

    def range = new RangeExpectsQueryOrFilter
    class RangeExpectsQueryOrFilter {
        def query(field: String): RangeQueryBuilder = new RangeQueryBuilder(field)
    }

    def regex = new RegexExpectsQueryOrFilter
    class RegexExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = RegexQueryBuilder
        def query(field: String, value: Any): RegexQueryBuilder = new RegexQueryBuilder(field, value)
    }

    def string = new StringExpectsQueryOrFilter
    class StringExpectsQueryOrFilter {
        def query(q: String) = new StringQueryBuilder(q)
    }

    def term = new TermExpectsQueryOrFilter
    class TermExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = TermQueryBuilder
        def query(field: String, value: Any): TermQueryBuilder = new TermQueryBuilder(field, value)
    }

    def term(field: String, value: Any): TermQueryBuilder = {
        val builder = new TermQueryBuilder(field, value)
        builder
    }

}

