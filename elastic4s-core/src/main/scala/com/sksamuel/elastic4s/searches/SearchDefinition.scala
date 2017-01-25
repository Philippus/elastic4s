package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.script.ScriptFieldDefinition
import com.sksamuel.elastic4s.searches.aggs.AggregationDefinition
import com.sksamuel.elastic4s.searches.queries._
import com.sksamuel.elastic4s.searches.queries.`match`.MatchAllQueryDefinition
import com.sksamuel.elastic4s.searches.sort.SortDefinition
import com.sksamuel.elastic4s.searches.suggestion.SuggestionDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.action.support.IndicesOptions
import org.elasticsearch.cluster.routing.Preference

import scala.concurrent.duration.{Duration, FiniteDuration}

case class Highlight(options: HighlightOptionsDefinition,
                     fields: Iterable[HighlightFieldDefinition])

case class SearchDefinition(indexesTypes: IndexesAndTypes,
                            query: Option[QueryDefinition] = None,
                            postFilter: Option[QueryDefinition] = None,
                            minScore: Option[Double] = None,
                            requestCache: Option[Boolean] = None,
                            storedFields: Seq[String] = Nil,
                            aggs: Seq[AggregationDefinition] = Nil,
                            sorts: Seq[SortDefinition[_]] = Nil,
                            scriptFields: Seq[ScriptFieldDefinition] = Nil,
                            suggs: Seq[SuggestionDefinition] = Nil,
                            highlight: Option[Highlight] = None,
                            explain: Option[Boolean] = None,
                            from: Option[Int] = None,
                            size: Option[Int] = None,
                            pref: Option[String] = None,
                            routing: Option[String] = None,
                            indicesOptions: Option[IndicesOptions] = None,
                            version: Option[Boolean] = None,
                            stats: Seq[String] = Nil,
                            trackScores: Option[Boolean] = None,
                            terminateAfter: Option[Int] = None,
                            rescorers: Seq[RescoreDefinition] = Nil,
                            searchType: Option[SearchType] = None,
                            timeout: Option[Duration] = None,
                            scroll: Option[String] = None,
                            indexBoosts: Seq[(String, Double)] = Nil
                           ) {

  /** Adds a single string query to this search
    *
    * @param string the query string
    */
  def query(string: String): SearchDefinition = query(QueryStringQueryDefinition(string))

  def query(q: QueryDefinition): SearchDefinition = copy(query = q.some)

  // adds a query to this search
  def query(block: => QueryDefinition): SearchDefinition = copy(query = block.some)

  def minScore(min: Double): SearchDefinition = copy(minScore = min.some)

  def bool(block: => BoolQueryDefinition): SearchDefinition = query(block)

  /**
    * Adds a match all query to this search definition
    */
  def matchAll(): SearchDefinition = query(new MatchAllQueryDefinition)

  // todo restore inner
  //  def inner(inners: InnerHitDefinition*): SearchDefinition =  inner(inners)
  //  def inner(inners: Iterable[InnerHitDefinition]): SearchDefinition =  {
  //    for ( inner <- inners )
  //      _builder.addInnerHit(inner.name, inner.inner)
  //    this
  //  }

  def postFilter(block: => QueryDefinition): SearchDefinition = copy(postFilter = block.some)

  def requestCache(requestCache: Boolean): SearchDefinition = copy(requestCache = requestCache.some)

  def aggregations(aggs: Iterable[AggregationDefinition]): SearchDefinition = copy(aggs = aggs.toSeq)

  def aggregations(first: AggregationDefinition,
                   rest: AggregationDefinition*): SearchDefinition = aggregations(first +: rest)

  def aggs(first: AggregationDefinition, rest: AggregationDefinition*): SearchDefinition = aggregations(first +: rest)
  def aggs(iterable: Iterable[AggregationDefinition]): SearchDefinition = aggregations(iterable)

  @deprecated("use sortBy", "5.0.0")
  def sort(sorts: SortDefinition[_]*): SearchDefinition = sortBy(sorts)

  def sortBy(sorts: SortDefinition[_]*): SearchDefinition = sortBy(sorts)

  def sortBy(sorts: Iterable[SortDefinition[_]]): SearchDefinition = copy(sorts = sorts.toSeq)

  /** This method introduces zero or more script field definitions into the search construction
    *
    * @param fields zero or more [[ScriptFieldDefinition]] instances
    * @return this, an instance of [[SearchDefinition]]
    */
  def scriptfields(fields: ScriptFieldDefinition*): SearchDefinition = scriptfields(fields)
  def scriptfields(fields: Iterable[ScriptFieldDefinition]): SearchDefinition = {
    //    defs.foreach {
    //      case ScriptFieldDefinition(name, script, None, None, _, ScriptType.INLINE) =>
    //        _builder.addScriptField(name, new Script(script))
    //      case ScriptFieldDefinition(name, script, lang, params, options, scriptType) =>
    //        _builder.addScriptField(name, new Script(scriptType, lang.getOrElse(Script.DEFAULT_SCRIPT_LANG), script,
    //                                                 options.map(_.asJava).getOrElse(new util.HashMap()),
    //                                                 params.map(_.asJava).getOrElse(new util.HashMap())))
    //    }
    //    this
    copy(scriptFields = fields.toSeq)
  }

  /**
    * Adds a new suggestion to the search request, which can be looked up in the response
    * using the name provided.
    */
  def suggestions(first: SuggestionDefinition,
                  rest: SuggestionDefinition*): SearchDefinition = suggestions(first +: rest)

  def suggestions(suggs: Iterable[SuggestionDefinition]): SearchDefinition = copy(suggs = suggs.toSeq)

  def suggestion(sugg: SuggestionDefinition): SearchDefinition = suggestions(Seq(sugg))


  /** Adds a single prefix query to this search
    *
    * @param tuple - the field and prefix value
    * @return this
    */
  def prefix(tuple: (String, Any)) = query(PrefixQueryDefinition(tuple._1, tuple._2))

  /** Adds a single regex query to this search
    *
    * @param tuple - the field and regex value
    * @return this
    */
  @deprecated("use regexQuery(...)", "5.0.0")
  def regex(tuple: (String, String)) = regexQuery(tuple)

  def regexQuery(tuple: (String, String)): SearchDefinition = regexQuery(tuple._1, tuple._2)
  def regexQuery(field: String, value: String): SearchDefinition = query(RegexQueryDefinition(field, value))

  @deprecated("use termQuery()", "5.0.0")
  def term(tuple: (String, Any)): SearchDefinition = termQuery(tuple)
  @deprecated("use termQuery()", "5.0.0")
  def term(field: String, value: Any): SearchDefinition = termQuery(field, value)

  def termQuery(tuple: (String, Any)): SearchDefinition = termQuery(tuple._1, tuple._2)
  def termQuery(field: String, value: Any): SearchDefinition = {
    val q = TermQueryDefinition(field, value)
    query(q)
    this
  }

  def range(field: String) = query(RangeQueryDefinition(field))

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
  def rawQuery(json: String): SearchDefinition = query(RawQueryDefinition(json))

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
  def extraSource(json: String): SearchDefinition = ??? // todo

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
  def source(json: String): SearchDefinition = ??? // todo

  def explain(enabled: Boolean): SearchDefinition = copy(explain = enabled.some)

  def highlighting(first: HighlightFieldDefinition,
                   rest: HighlightFieldDefinition*): SearchDefinition =
    highlighting(HighlightOptionsDefinition(), first +: rest)

  def highlighting(fields: Iterable[HighlightFieldDefinition]): SearchDefinition =
    highlighting(HighlightOptionsDefinition(), fields)

  def highlighting(options: HighlightOptionsDefinition,
                   first: HighlightFieldDefinition,
                   rest: HighlightFieldDefinition*): SearchDefinition = highlighting(options, first +: rest)

  def highlighting(options: HighlightOptionsDefinition,
                   fields: Iterable[HighlightFieldDefinition]): SearchDefinition =
    copy(highlight = Highlight(options, fields).some)

  def routing(r: String): SearchDefinition = copy(routing = r.some)

  def start(i: Int): SearchDefinition = from(i)
  def from(i: Int): SearchDefinition = copy(from = i.some)

  def limit(i: Int): SearchDefinition = size(i)
  def size(i: Int): SearchDefinition = copy(size = i.some)

  def preference(pref: com.sksamuel.elastic4s.Preference): SearchDefinition = preference(pref.value)
  def preference(pref: Preference): SearchDefinition = preference(pref.`type`)
  def preference(pref: String): SearchDefinition = copy(pref = pref.some)

  def indicesOptions(options: IndicesOptions): SearchDefinition = copy(indicesOptions = options.some)

  def rescore(first: RescoreDefinition, rest: RescoreDefinition*): SearchDefinition = rescore(first +: rest)
  def rescore(rescorers: Iterable[RescoreDefinition]): SearchDefinition = copy(rescorers = rescorers.toSeq)

  def scroll(keepAlive: String): SearchDefinition = copy(scroll = keepAlive.some)

  def searchType(searchType: SearchType) = copy(searchType = searchType.some)

  def version(version: Boolean): SearchDefinition = copy(version = version.some)

  def terminateAfter(terminateAfter: Int): SearchDefinition = copy(terminateAfter = terminateAfter.some)

  def indexBoost(map: Map[String, Double]): SearchDefinition = indexBoost(map.toList: _*)
  def indexBoost(tuples: (String, Double)*): SearchDefinition = copy(indexBoosts = tuples)

  def timeout(duration: FiniteDuration): SearchDefinition = copy(timeout = duration.some)
  def stats(groups: String*): SearchDefinition = copy(stats = groups.toSeq)

  def trackScores(enabled: Boolean): SearchDefinition = copy(trackScores = enabled.some)

  @deprecated("Renamed to storedFields", "5.0.0")
  def fields(fields: String*): SearchDefinition = storedFields(fields)

  def storedFields(first: String, rest: String*): SearchDefinition = storedFields(first +: rest)
  def storedFields(fields: Iterable[String]): SearchDefinition = copy(storedFields = fields.toSeq)

  def fetchSource(fetch: Boolean): SearchDefinition = {
    _builder.setFetchSource(fetch)
    this
  }

  def sourceInclude(first: String, rest: String*): SearchDefinition = sourceInclude(first +: rest)
  def sourceInclude(includes: Iterable[String]): SearchDefinition = {
    this.includes = includes.toArray
    _builder.setFetchSource(this.includes, this.excludes)
    this
  }

  def sourceExclude(first: String, rest: String*): SearchDefinition = sourceExclude(first +: rest)
  def sourceExclude(excludes: Iterable[String]): SearchDefinition = {
    this.excludes = excludes.toArray
    _builder.setFetchSource(this.includes, this.excludes)
    this
  }

  def sourceFiltering(includes: Iterable[String], excludes: Iterable[String]): SearchDefinition = {
    this.includes = includes.toArray
    this.excludes = excludes.toArray
    _builder.setFetchSource(this.includes, this.excludes)
    this
  }
}
