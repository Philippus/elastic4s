package com.sksamuel.elastic4s

import org.elasticsearch.action.search.SearchRequestBuilder

/** @author Stephen Samuel */
object SearchDsl extends QueryDsl with FilterDsl {

    abstract class Facet(name: String) {
        val global: Boolean = false
        def builder: org.elasticsearch.search.facet.FacetBuilder
    }

    def preTag(tag: String) = this
    def postTag(tag: String) = this

    def highlight = new HighlightBuilder
    def highlight(field: String) = new HighlightBuilder

    class SearchBuilder(indexes: Seq[String], types: Seq[String]) {

        val builder = new SearchRequestBuilder(null).setIndices(indexes: _*).setTypes(types: _*)

        def query(block: => QueryDefinition): SearchBuilder = {
            builder.setQuery(block.builder)
            this
        }

        def filter(block: => FilterDefinition): SearchBuilder = {
            builder.setFilter(block.builder)
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
            val q = new RangeQueryDefinition(field)
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

        def preference(pref: String) = {
            builder.setPreference(pref)
            this
        }

        def preference(pref: Preference) = {
            builder.setPreference(pref.elastic)
            this
        }

        def scroll(keepAlive: String) = {
            builder.setScroll(keepAlive)
            this
        }

        def indexBoost(tuples: (String, Double)*) = this
        def indexBoost(map: Map[String, Double]) = this

        def explain(enabled: Boolean) = {
            builder.setExplain(enabled)
            this
        }

        def minStore(score: Double) = {
            builder.setMinScore(score.toFloat)
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
}

