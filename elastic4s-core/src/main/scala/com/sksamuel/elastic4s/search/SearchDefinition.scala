package com.sksamuel.elastic4s.search

import java.util

import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.query.{BoolQueryDefinition, FuzzyQueryDefinition, PrefixQueryDefinition, QueryStringQueryDefinition, RangeQueryDefinition, RegexQueryDefinition, TermQueryDefinition}
import com.sksamuel.elastic4s.sort.SortDefinition
import org.elasticsearch.action.search.{SearchAction, SearchRequestBuilder}
import org.elasticsearch.action.support.IndicesOptions
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders}
import org.elasticsearch.script.{Script, ScriptService}
import org.elasticsearch.search.sort.SortBuilder

import scala.concurrent.duration.FiniteDuration

case class SearchDefinition(indexesTypes: IndexesAndTypes) {

  val _builder = {
    new SearchRequestBuilder(ProxyClients.client, SearchAction.INSTANCE)
      .setIndices(indexesTypes.indexes: _*)
      .setTypes(indexesTypes.types: _*)
  }
  def build = _builder.request()

  private var includes: Array[String] = Array.empty
  private var excludes: Array[String] = Array.empty

  /** Adds a single string query to this search
    *
    * @param string the query string
    */
  def query(string: String): SearchDefinition = query(QueryStringQueryDefinition(string))
  def query(block: => QueryDefinition): SearchDefinition = query2(block.builder)
  def query2(block: => QueryBuilder): SearchDefinition = {
    _builder.setQuery(block)
    this
  }

  def minScore(min: Double): this.type = {
    _builder.setMinScore(min.toFloat)
    this
  }

  def bool(block: => BoolQueryDefinition): SearchDefinition = {
    _builder.setQuery(block.builder)
    this
  }

  // todo restore inner
  //  def inner(inners: InnerHitDefinition*): this.type = inner(inners)
  //  def inner(inners: Iterable[InnerHitDefinition]): this.type = {
  //    for ( inner <- inners )
  //      _builder.addInnerHit(inner.name, inner.inner)
  //    this
  //  }

  def postFilter(block: => QueryDefinition): this.type = {
    _builder.setPostFilter(block.builder)
    this
  }

  def requestCache(requestCache: Boolean): this.type = {
    _builder.setRequestCache(requestCache)
    this
  }

  //  def aggregations(iterable: Iterable[AbstractAggregationDefinition]): SearchDefinition = {
  //    iterable.foreach(agg => _builder.addAggregation(agg.builder))
  //    this
  //  }
  //  def aggregations(a: AbstractAggregationDefinition*): SearchDefinition = aggregations(a.toIterable)
  //  def aggs(first: AbstractAggregationDefinition, rest: AbstractAggregationDefinition*): SearchDefinition = {
  //    aggregations(first +: rest)
  //  }
  //  def aggs(iterable: Iterable[AbstractAggregationDefinition]): SearchDefinition = aggregations(iterable)
  //
  //  def aggregations(json: String): this.type = {
  //    _builder.setAggregations(json.getBytes("UTF-8"))
  //    this
  //  }

  def sort[T <: SortBuilder[T]](sorts: SortDefinition[T]*): SearchDefinition = {
    sorts.map(_.builder).foreach(_builder.request().source().sort)
    this
  }

  /** This method introduces zero or more script field definitions into the search construction
    *
    * @param sfieldDefs zero or more [[ScriptFieldDefinition]] instances
    * @return this, an instance of [[SearchDefinition]]
    */
  def scriptfields(sfieldDefs: ScriptFieldDefinition*): this.type = {
    import scala.collection.JavaConverters._
    sfieldDefs.foreach {
      case ScriptFieldDefinition(name, script, Some(lang), Some(params), scriptType) =>
        _builder.addScriptField(name, new Script(script, scriptType, lang, params.asJava))
      case ScriptFieldDefinition(name, script, Some(lang), None, scriptType) =>
        _builder.addScriptField(name, new Script(script, scriptType, lang, new util.HashMap()))
      case ScriptFieldDefinition(name, script, None, Some(params), scriptType) =>
        _builder.addScriptField(name, new Script(script, scriptType, null, params.asJava))
      case ScriptFieldDefinition(name, script, None, None, ScriptService.ScriptType.INLINE) =>
        _builder.addScriptField(name, new Script(script))
      case ScriptFieldDefinition(name, script, None, None, scriptType) =>
        _builder.addScriptField(name, new Script(script, scriptType, null, new util.HashMap()))
    }
    this
  }

  //  def suggestions(suggestions: SuggestionDefinition*): SearchDefinition = {
  //    suggestions.foreach(_builder addSuggestion _.builder)
  //    this
  //  }

  /** Adds a single prefix query to this search
    *
    * @param tuple - the field and prefix value
    *
    * @return this
    */
  def prefix(tuple: (String, Any)) = {
    val q = PrefixQueryDefinition(tuple._1, tuple._2)
    _builder.setQuery(QueryBuilders.wrapperQuery(q.builder.buildAsBytes))
    this
  }

  /** Adds a single regex query to this search
    *
    * @param tuple - the field and regex value
    *
    * @return this
    */
  def regex(tuple: (String, Any)) = {
    val q = RegexQueryDefinition(tuple._1, tuple._2)
    _builder.setQuery(QueryBuilders.wrapperQuery(q.builder.buildAsBytes))
    this
  }

  def term(tuple: (String, Any)) = {
    val q = TermQueryDefinition(tuple._1, tuple._2)
    _builder.setQuery(QueryBuilders.wrapperQuery(q.builder.buildAsBytes))
    this
  }

  def range(field: String) = {
    val q = RangeQueryDefinition(field)
    _builder.setQuery(QueryBuilders.wrapperQuery(q.builder.buildAsBytes))
    this
  }

  /** Expects a query in json format and sets the query of the search request.
    * i.e. underneath a "query" field if referencing HTTP API
    * Query must be valid json beginning with '{' and ending with '}'.
    * Field names must be double quoted.
    *
    * Example:
    * {{{
    * search in "*" types("users", "tweets") limit 5 rawQuery {
    * """{ "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } }"""
    * } searchType SearchType.Scan
    * }}}
    */
  def rawQuery(json: String): SearchDefinition = {
    _builder.setQuery(QueryBuilders.wrapperQuery(json))
    this
  }

  /** Sets the source of the request as a json string. Allows setting other parameters.
    * Unlike rawQuery, setExtraSource is parsed at the "root" level
    * Query must be valid json beginning with '{' and ending with '}'.
    * Field names must be double quoted.
    *
    * Example:
    * {{{
    * search in "*" types("users", "tweets") limit 5 extraSource {
    * """{ "query": { "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } } }"""
    * } searchType SearchType.Scan
    * }}}
    */
  def extraSource(json: String): SearchDefinition = {
    // todo
    // _builder.setExtraSource(json)
    this
  }

  /**
    * Sets the source of the request as a json string. Note, setting anything other
    * than the search type will cause this source to be overridden, consider using
    * {@link #setExtraSource(String)}.
    *
    * Unlike rawQuery, setExtraSource is parsed at the "root" level
    * Query must be valid json beginning with '{' and ending with '}'.
    * Field names must be double quoted.
    *
    * Example:
    * {{{
    * search in "*" types("users", "tweets") limit 5 extraSource {
    * """{ "query": { "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } } }"""
    * } searchType SearchType.Scan
    * }}}
    */
  def source(json: String): SearchDefinition = {
    // todo
    // _builder.setSource(SearchSourceBuilder.fromXContent() json)
    this
  }

  def explain(enabled: Boolean): SearchDefinition = {
    _builder.setExplain(enabled)
    this
  }

  @deprecated("Set fuzzy query using query(fuzzyQuery(...)) syntax", "3.0.0")
  def fuzzy(tuple: (String, Any)) = {
    val q = FuzzyQueryDefinition(tuple._1, tuple._2)
    _builder.setQuery(QueryBuilders.wrapperQuery(q.builder.buildAsBytes))
    this
  }

  //  def highlighting(options: HighlightOptionsDefinition, highlights: HighlightDefinition*) = {
  //    options._encoder.foreach(encoder => _builder.setHighlighterEncoder(encoder.elastic))
  //    options._tagSchema.foreach(arg => _builder.setHighlighterTagsSchema(arg.elastic))
  //    options._order.foreach(arg => _builder.setHighlighterOrder(arg.elastic))
  //    _builder.setHighlighterPostTags(options._postTags: _*)
  //    _builder.setHighlighterPreTags(options._preTags: _*)
  //    _builder.setHighlighterRequireFieldMatch(options._requireFieldMatch)
  //    highlights.foreach(highlight => _builder.addHighlightedField(highlight.builder))
  //    this
  //  }
  //
  //  def highlighting(highlights: HighlightDefinition*): SearchDefinition = {
  //    highlights.foreach(highlight => _builder.addHighlightedField(highlight.builder))
  //    this
  //  }

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

  def indicesOptions(options: IndicesOptions): SearchDefinition = {
    _builder.setIndicesOptions(options)
    this
  }

  def rescore(rescorers: RescoreDefinition*): SearchDefinition = {
    rescorers.foreach(rescorer => _builder.addRescorer(rescorer.builder))
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

  def terminateAfter(terminateAfter: Int): SearchDefinition = {
    _builder.setTerminateAfter(terminateAfter)
    this
  }

  def indexBoost(map: Map[String, Double]): SearchDefinition = indexBoost(map.toList: _*)
  def indexBoost(tuples: (String, Double)*): SearchDefinition = {
    tuples.foreach(arg => _builder.addIndexBoost(arg._1, arg._2.toFloat))
    this
  }

  def timeout(duration: FiniteDuration): this.type = {
    _builder.setTimeout(TimeValue.timeValueMillis(duration.toMillis))
    this
  }

  def stats(groups: String*): this.type = {
    _builder.setStats(groups: _*)
    this
  }

  def trackScores(enabled: Boolean): SearchDefinition = {
    _builder.setTrackScores(enabled)
    this
  }

  def types(types: String*): SearchDefinition = {
    _builder.setTypes(types: _*)
    this
  }

  def fields(fields: String*): SearchDefinition = {
    _builder.storedFields(fields: _*)
    this
  }

  def fetchSource(fetch: Boolean): SearchDefinition = {
    _builder.setFetchSource(fetch)
    this
  }

  def sourceInclude(includes: String*): this.type = {
    this.includes = includes.toArray
    _builder.setFetchSource(this.includes, this.excludes)
    this
  }

  def sourceExclude(excludes: String*): this.type = {
    this.excludes = excludes.toArray
    _builder.setFetchSource(this.includes, this.excludes)
    this
  }

  override def toString = _builder.toString
}
