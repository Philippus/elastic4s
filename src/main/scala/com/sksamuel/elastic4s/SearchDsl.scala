package com.sksamuel.elastic4s

import org.elasticsearch.action.search._
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.rescore.RescoreBuilder

/** @author Stephen Samuel */
trait SearchDsl
  extends QueryDsl
  with FilterDsl
  with FacetDsl
  with AggregationDsl
  with HighlightDsl
  with SortDsl
  with SuggestionDsl
  with IndexesTypesDsl {

  @deprecated("use select or search", "1.0")
  def find = new SearchExpectsIndex

  def select: SearchExpectsIndex = search
  def select(indexes: String*): SearchDefinition = search(indexes: _*)
  def search: SearchExpectsIndex = new SearchExpectsIndex
  def search(indexes: String*): SearchDefinition = new SearchDefinition(IndexesTypes(indexes))
  class SearchExpectsIndex {
    def in(indexes: String*): SearchDefinition = in(IndexesTypes(indexes))
    def in(tuple: (String, String)): SearchDefinition = in(IndexesTypes(tuple))
    def in(indexesTypes: IndexesTypes): SearchDefinition = new SearchDefinition(indexesTypes)
  }

  class MultiSearchDefinition(searches: Iterable[SearchDefinition])
    extends RequestDefinition(MultiSearchAction.INSTANCE) {
    def build = {
      val builder = new MultiSearchRequestBuilder(null)
      searches foreach (builder add _.build)
      builder.request()
    }
  }

  def rescore(query: QueryDefinition): RescoreDefinition = {
    new RescoreDefinition(query)
  }

  class RescoreDefinition(query: QueryDefinition) {
    val builder = RescoreBuilder.queryRescorer(query.builder)
    var windowSize = 50

    def window(size: Int): RescoreDefinition = {
      this.windowSize = size
      this
    }

    def originalQueryWeight(weight: Double): RescoreDefinition = {
      builder.setQueryWeight(weight.toFloat)
      this
    }

    def rescoreQueryWeight(weight: Double): RescoreDefinition = {
      builder.setRescoreQueryWeight(weight.toFloat)
      this
    }

    def scoreMode(scoreMode: String): RescoreDefinition = {
      builder.setScoreMode(scoreMode)
      this
    }
  }

  class SearchDefinition(indexesTypes: IndexesTypes) extends RequestDefinition(SearchAction.INSTANCE) {

    val _builder = {
      new SearchRequestBuilder(null)
        .setIndices(indexesTypes.indexes: _*)
        .setTypes(indexesTypes.types: _*)
    }
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
    def bool(block: => BoolQueryDefinition): SearchDefinition = {
      _builder.setQuery(block.builder)
      this
    }

    def filter(block: => FilterDefinition): SearchDefinition = {
      _builder.setPostFilter(block.builder)
      this
    }

    def facets(iterable: Iterable[FacetDefinition]): SearchDefinition = {
      iterable.foreach(facet => _builder.addFacet(facet.builder))
      this
    }
    def facets(f: FacetDefinition*): SearchDefinition = facets(f.toIterable)

    def aggregations(iterable: Iterable[AbstractAggregationDefinition]): SearchDefinition = {
      iterable.foreach(agg => _builder.addAggregation(agg.builder))
      this
    }
    def aggregations(a: AbstractAggregationDefinition*): SearchDefinition = aggregations(a.toIterable)
    def aggs(a: AbstractAggregationDefinition*): SearchDefinition = aggregations(a.toIterable)
    def aggs(iterable: Iterable[AbstractAggregationDefinition]): SearchDefinition = aggregations(iterable)

    def sort(sorts: SortDefinition*): SearchDefinition = sort2(sorts.map(_.builder): _*)
    def sort2(sorts: SortBuilder*): SearchDefinition = {
      sorts.foreach(_builder addSort)
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

    /**
      * Expects a query in json format and sets the query of the search request.
      * Query must be valid json beginning with '{' and ending with '}'.
      * Field names must be double quoted.
      *
      * Example:
      * {{{
      * search in "*" types("users", "tweets") limit 5 rawQuery {
      *   """{ "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } }"""
      * } searchType SearchType.Scan
      * }}}
      */
    def rawQuery(json: String): SearchDefinition = {
      _builder.setQuery(json)
      this
    }

    def highlighting(options: HighlightOptionsDefinition, highlights: HighlightDefinition*) = {
      options._encoder.foreach(encoder => _builder.setHighlighterEncoder(encoder.elastic))
      options._tagSchema.foreach(arg => _builder.setHighlighterTagsSchema(arg.elastic))
      options._order.foreach(arg => _builder.setHighlighterOrder(arg.elastic))
      _builder.setHighlighterPostTags(options._postTags: _*)
      _builder.setHighlighterPreTags(options._preTags: _*)
      _builder.setHighlighterRequireFieldMatch(options._requireFieldMatch)
      highlights.foreach(highlight => _builder.addHighlightedField(highlight.builder))
      this
    }

    def highlighting(highlights: HighlightDefinition*): SearchDefinition = {
      highlights.foreach(highlight => _builder.addHighlightedField(highlight.builder))
      this
    }

    def routing(r: String): SearchDefinition = {
      _builder.setRouting(r)
      this
    }

    def start(i: Int): SearchDefinition = from(i)
    def from(i: Int): SearchDefinition = {
      _builder.setFrom(i)
      this
    }

    def limit(i: Int): SearchDefinition = size(i)
    def size(i: Int): SearchDefinition = {
      _builder.setSize(i)
      this
    }

    def preference(pref: Preference): SearchDefinition = preference(pref.elastic)
    def preference(pref: String): SearchDefinition = {
      _builder.setPreference(pref)
      this
    }

    def rescore(rescore: RescoreDefinition): SearchDefinition = {
      _builder.setRescoreWindow(rescore.windowSize)
      _builder.setRescorer(rescore.builder)
      this
    }

    def scroll(keepAlive: String): SearchDefinition = {
      _builder.setScroll(keepAlive)
      this
    }

    def searchType(searchType: SearchType) = {
      _builder.setSearchType(searchType.elasticType)
      this
    }

    def version(enabled: Boolean): SearchDefinition = {
      _builder.setVersion(enabled)
      this
    }

    def indexBoost(map: Map[String, Double]): SearchDefinition = indexBoost(map.toList: _*)
    def indexBoost(tuples: (String, Double)*): SearchDefinition = {
      tuples.foreach(arg => _builder.addIndexBoost(arg._1, arg._2.toFloat))
      this
    }

    def explain(enabled: Boolean): SearchDefinition = {
      _builder.setExplain(enabled)
      this
    }

    def minScore(score: Double): SearchDefinition = {
      _builder.setMinScore(score.toFloat)
      this
    }

    def types(types: String*): SearchDefinition = {
      _builder.setTypes(types: _*)
      this
    }

    def fields(fields: String*): SearchDefinition = {
      _builder.addFields(fields: _*)
      this
    }

    def fetchSource(fetch: Boolean): SearchDefinition = {
      _builder.setFetchSource(fetch)
      this
    }
  }
}

