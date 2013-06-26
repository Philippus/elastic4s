package com.sksamuel.elastic4s

import org.elasticsearch.action.search.SearchRequestBuilder

/** @author Stephen Samuel */
object SearchDsl extends QueryDsl {

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

        def query(block: => QueryDefinition): SearchBuilder = {
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
            val q = new StringQueryDefinition(string)
            builder.setQuery(q.builder.buildAsBytes)
            this
        }

        /**
         * Adds a single prefix query to this search
         *
         * @param tuple - the field and prefix value
         *
         * @return this
         */
        def prefix(tuple: (String, Any)) = {
            val q = new PrefixQueryDefinition(tuple._1, tuple._2)
            builder.setQuery(q.builder.buildAsBytes)
            this
        }

        /**
         * Adds a single regex query to this search
         *
         * @param tuple - the field and regex value
         *
         * @return this
         */
        def regex(tuple: (String, Any)) = {
            val q = new RegexQueryDefinition(tuple._1, tuple._2)
            builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def term(tuple: (String, Any)) = {
            val q = new TermQueryDefinition(tuple._1, tuple._2)
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
        def q(args: String*): StringQueryDefinition = new StringQueryDefinition(args.head)
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
        type QueryBuilder = MatchQueryDefinition
        def query(field: String, value: Any): MatchQueryDefinition = new MatchQueryDefinition(field, value)
    }

    def all = matchAll()
    def matchAll() = {
        val builder = new MatchAllQueryDefinition()
        builder
    }

    def prefix = new PrefixExpectsQueryOrFilter
    class PrefixExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = PrefixQueryDefinition
        def query(field: String, value: Any): PrefixQueryDefinition = new PrefixQueryDefinition(field, value)
    }

    def range = new RangeExpectsQueryOrFilter
    class RangeExpectsQueryOrFilter {
        def query(field: String): RangeQueryBuilder = new RangeQueryBuilder(field)
    }

    def regex = new RegexExpectsQueryOrFilter
    class RegexExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = RegexQueryDefinition
        def query(field: String, value: Any): RegexQueryDefinition = new RegexQueryDefinition(field, value)
    }

    def bool(block: => QueryBuilders): QueryDefinition = {
        null
    }

    def must(builders: QueryDefinition*): BoolQueryDefinition = {
        null
    }

    class BoolQueryDefinition {
        def and(minShould: Int) = {
            this
        }
    }

    def should(block: => QueryBuilders): QueryBuilders = {
        null
    }

    def not(block: => QueryBuilders): QueryBuilders = {
        null
    }

    class QueryBuilders {
        def should(block: => QueryBuilders): QueryBuilders = {
            null
        }

        def not(block: => QueryBuilders): QueryBuilders = {
            null
        }

        def and(builder: QueryDefinition) {

        }
    }

    class CompoundQueryBuilder {

    }

    class ExpectsQuery {
        def regex(tuple: (String, Any)) = new RegexQueryDefinition(tuple._1, tuple._2)
    }

    def string = new StringExpectsQueryOrFilter
    class StringExpectsQueryOrFilter {
        def query(q: String) = new StringQueryDefinition(q)
    }

    def term = new TermExpectsQueryOrFilter
    class TermExpectsQueryOrFilter extends PairQuery {
        type QueryBuilder = TermQueryDefinition
        def query(field: String, value: Any): TermQueryDefinition = new TermQueryDefinition(field, value)
    }

}

