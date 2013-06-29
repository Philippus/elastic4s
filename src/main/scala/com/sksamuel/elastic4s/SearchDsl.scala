package com.sksamuel.elastic4s

import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.index.query.QueryBuilder

/** @author Stephen Samuel */
trait SearchDsl extends QueryDsl with FilterDsl with SortDsl with SuggestionDsl {

    def find = new SearchExpectsIndex
    def select = new SearchExpectsIndex
    def search = new SearchExpectsIndex
    class SearchExpectsIndex {
        def in(indexes: String*): SearchDefinition = new SearchDefinition(indexes)
        def in(tuple: (String, String)): SearchDefinition = new SearchDefinition(Seq(tuple._1)).types(tuple._2)
    }

    abstract class Facet(name: String) {
        val global: Boolean = false
        def builder: org.elasticsearch.search.facet.FacetBuilder
    }

    def preTag(tag: String) = this
    def postTag(tag: String) = this

    def highlight = new HighlightBuilder
    def highlight(field: String) = new HighlightBuilder

    class SearchDefinition(indexes: Seq[String]) {

        val _builder = new SearchRequestBuilder(null).setIndices(indexes: _*)
        def build = _builder.request()

        /**
         * Adds a single string query to this search
         *
         * @param string the query string
         *
         * @return this
         */
        def query(string: String): SearchDefinition = query(new StringQueryDefinition(string))
        def query(block: => QueryDefinition): SearchDefinition = query2(block.builder)
        def query2(block: => QueryBuilder): SearchDefinition = {
            _builder.setQuery(block)
            this
        }

        def filter(block: => FilterDefinition): SearchDefinition = {
            _builder.setFilter(block.builder)
            this
        }

        def sort(sorts: SortDefinition*): SearchDefinition = {
            sorts.foreach(_builder addSort _.builder)
            this
        }

        def suggestions(suggestions: SuggestionDefinition*): SearchDefinition = {
            suggestions.foreach(_builder addSuggestion _.builder)
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
            _builder.setQuery(q.builder.buildAsBytes)
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
            _builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def term(tuple: (String, Any)) = {
            val q = new TermQueryDefinition(tuple._1, tuple._2)
            _builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def range(field: String) = {
            val q = new RangeQueryDefinition(field)
            _builder.setQuery(q.builder.buildAsBytes)
            this
        }

        def highlight(field: String) = new HighlightBuilder

        def highlighting(block: => Unit) {

        }

        def routing(r: String) = {
            _builder.setRouting(r)
            this
        }

        def start(i: Int) = from(i)
        def from(i: Int) = {
            _builder.setFrom(i)
            this
        }

        def limit(i: Int) = size(i)
        def size(i: Int) = {
            _builder.setSize(i)
            this
        }

        def searchType(searchType: SearchType) = {
            _builder.setSearchType(searchType.elasticType)
            this
        }

        def version(enabled: Boolean) = {
            _builder.setVersion(enabled)
            this
        }

        def preference(pref: String) = {
            _builder.setPreference(pref)
            this
        }

        def preference(pref: Preference) = {
            _builder.setPreference(pref.elastic)
            this
        }

        def scroll(keepAlive: String) = {
            _builder.setScroll(keepAlive)
            this
        }

        def indexBoost(tuples: (String, Double)*) = this
        def indexBoost(map: Map[String, Double]) = this

        def explain(enabled: Boolean) = {
            _builder.setExplain(enabled)
            this
        }

        def minStore(score: Double) = {
            _builder.setMinScore(score.toFloat)
            this
        }

        def types(types: String*): SearchDefinition = {
            _builder.setTypes(types: _*)
            this
        }
    }

    class Highlighter(request: SearchRequestBuilder) extends SearchDefinition(null) {
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

}

