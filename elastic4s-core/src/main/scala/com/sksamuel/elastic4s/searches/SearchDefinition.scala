package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.script.ScriptFieldDefinition
import com.sksamuel.elastic4s.searches.aggs.AbstractAggregation
import com.sksamuel.elastic4s.searches.collapse.CollapseDefinition
import com.sksamuel.elastic4s.searches.queries._
import com.sksamuel.elastic4s.searches.queries.matches.{MatchAllQueryDefinition, MatchQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.term.TermQueryDefinition
import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, SortDefinition}
import com.sksamuel.elastic4s.searches.suggestion.SuggestionDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.action.support.IndicesOptions
import org.elasticsearch.cluster.routing.Preference
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

import scala.concurrent.duration.{Duration, FiniteDuration}

case class SearchDefinition(indexesTypes: IndexesAndTypes,
                            aggs: Seq[AbstractAggregation] = Nil,
                            collapse: Option[CollapseDefinition] = None,
                            explain: Option[Boolean] = None,
                            fetchContext: Option[FetchSourceContext] = None,
                            from: Option[Int] = None,
                            indicesOptions: Option[IndicesOptions] = None,
                            inners: Seq[InnerHitDefinition] = Nil,
                            indexBoosts: Seq[(String, Double)] = Nil,
                            keepAlive: Option[String] = None,
                            highlight: Option[Highlight] = None,
                            minScore: Option[Double] = None,
                            pref: Option[String] = None,
                            query: Option[QueryDefinition] = None,
                            postFilter: Option[QueryDefinition] = None,
                            requestCache: Option[Boolean] = None,
                            rescorers: Seq[RescoreDefinition] = Nil,
                            scriptFields: Seq[ScriptFieldDefinition] = Nil,
                            sorts: Seq[SortDefinition] = Nil,
                            storedFields: Seq[String] = Nil,
                            suggs: Seq[SuggestionDefinition] = Nil,
                            globalSuggestionText: Option[String] = None,
                            size: Option[Int] = None,
                            routing: Option[String] = None,
                            stats: Seq[String] = Nil,
                            searchType: Option[SearchType] = None,
                            searchAfter: Seq[Any] = Nil,
                            trackScores: Option[Boolean] = None,
                            terminateAfter: Option[Int] = None,
                            timeout: Option[Duration] = None,
                            version: Option[Boolean] = None
                           ) {

  /** Adds a single string query to this search
    *
    * @param string the query string
    */
  def query(string: String): SearchDefinition = query(QueryStringQueryDefinition(string))

  // adds a query to this search
  def query(q: QueryDefinition): SearchDefinition = copy(query = q.some)

  def minScore(min: Double): SearchDefinition = copy(minScore = min.some)

  def types(first: String, rest: String*): SearchDefinition = types(first +: rest)
  def types(types: Iterable[String]): SearchDefinition =
    copy(indexesTypes = IndexesAndTypes(indexesTypes.indexes, types.toSeq))

  def bool(block: => BoolQueryDefinition): SearchDefinition = query(block)

  @deprecated("Use matchAllQuery()", "5.2.0")
  def matchAll(): SearchDefinition = query(new MatchAllQueryDefinition)

  def inner(first: InnerHitDefinition, rest: InnerHitDefinition*): SearchDefinition = inner(first +: rest)
  def inner(inners: Iterable[InnerHitDefinition]): SearchDefinition = copy(inners = inners.toSeq)

  def searchAfter(values: Seq[Any]): SearchDefinition = copy(searchAfter = values)

  def postFilter(block: => QueryDefinition): SearchDefinition = copy(postFilter = block.some)

  def requestCache(requestCache: Boolean): SearchDefinition = copy(requestCache = requestCache.some)

  def aggs(first: AbstractAggregation, rest: AbstractAggregation*): SearchDefinition = aggs(first +: rest)
  def aggs(iterable: Iterable[AbstractAggregation]): SearchDefinition = aggregations(iterable)
  def aggregations(aggs: Iterable[AbstractAggregation]): SearchDefinition = copy(aggs = aggs.toSeq)
  def aggregations(first: AbstractAggregation, rest: AbstractAggregation*): SearchDefinition = aggregations(first +: rest)

  @deprecated("use sortBy", "5.0.0")
  def sort(sorts: SortDefinition*): SearchDefinition = sortBy(sorts)

  def sortBy(sorts: SortDefinition*): SearchDefinition = sortBy(sorts)
  def sortBy(sorts: Iterable[SortDefinition]): SearchDefinition = copy(sorts = sorts.toSeq)

  def sortByFieldAsc(name: String): SearchDefinition = sortBy(FieldSortDefinition(name))
  def sortByFieldDesc(name: String): SearchDefinition = sortBy(FieldSortDefinition(name).desc())

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

  def globalSuggestionText(text: String): SearchDefinition = copy(globalSuggestionText = text.some)

  // Adds a single prefix query to this search
  def prefix(name: String, value: Any): SearchDefinition = query(PrefixQueryDefinition(name, value))

  @deprecated("use regexQuery(...)", "5.0.0")
  def regex(tuple: (String, String)): SearchDefinition = regexQuery(tuple)
  def regexQuery(tuple: (String, String)): SearchDefinition = regexQuery(tuple._1, tuple._2)

  // Adds a single regex query to this search
  def regexQuery(field: String, value: String): SearchDefinition = query(RegexQueryDefinition(field, value))

  @deprecated("use termQuery()", "5.0.0")
  def term(tuple: (String, Any)): SearchDefinition = termQuery(tuple)
  @deprecated("use termQuery()", "5.0.0")
  def term(field: String, value: Any): SearchDefinition = termQuery(field, value)

  def termQuery(tuple: (String, Any)): SearchDefinition = termQuery(tuple._1, tuple._2)
  def termQuery(field: String, value: Any): SearchDefinition = {
    val q = TermQueryDefinition(field, value)
    query(q)
  }

  def matchQuery(field: String, value: Any): SearchDefinition = {
    val q = MatchQueryDefinition(field, value)
    query(q)
  }

  def matchAllQuery(): SearchDefinition = query(MatchAllQueryDefinition())

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

  @deprecated("Use the elasticsearch enum rather than the elastic4s one", "5.2.0")
  def preference(pref: com.sksamuel.elastic4s.Preference): SearchDefinition = preference(pref.value)
  def preference(pref: Preference): SearchDefinition = preference(pref.`type`)
  def preference(pref: String): SearchDefinition = copy(pref = pref.some)

  def indicesOptions(options: IndicesOptions): SearchDefinition = copy(indicesOptions = options.some)

  def rescore(first: RescoreDefinition, rest: RescoreDefinition*): SearchDefinition = rescore(first +: rest)
  def rescore(rescorers: Iterable[RescoreDefinition]): SearchDefinition = copy(rescorers = rescorers.toSeq)

  // alias for scroll
  def keepAlive(keepAlive: String): SearchDefinition = scroll(keepAlive)
  def scroll(keepAlive: String): SearchDefinition = copy(keepAlive = keepAlive.some)

  def searchType(searchType: SearchType): SearchDefinition = copy(searchType = searchType.some)

  def version(version: Boolean): SearchDefinition = copy(version = version.some)

  /**
    * The maximum number of documents to collect for each shard,
    * upon reaching which the query execution will terminate early.
    * If set, the response will have a boolean field terminated_early
    * to indicate whether the query execution has actually terminated
    * early. Defaults to no.
    */
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

  def fetchContext(context: FetchSourceContext): SearchDefinition = copy(fetchContext = context.some)
  def fetchSource(fetch: Boolean): SearchDefinition = copy(fetchContext = new FetchSourceContext(fetch).some)

  def sourceInclude(first: String, rest: String*): SearchDefinition = sourceFiltering(first +: rest, Nil)
  def sourceInclude(includes: Iterable[String]) : SearchDefinition = sourceFiltering(includes, Nil)

  def sourceExclude(first: String, rest: String*): SearchDefinition = sourceFiltering(Nil, first +: rest)
  def sourceExclude(excludes: Iterable[String]) : SearchDefinition = sourceFiltering(Nil, excludes)

  def sourceFiltering(includes: Iterable[String], excludes: Iterable[String]): SearchDefinition =
    copy(fetchContext = new FetchSourceContext(true, includes.toArray, excludes.toArray).some)

  def collapse(collapse: CollapseDefinition): SearchDefinition = copy(collapse = collapse.some)
}
