package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.admin.IndicesOptions
import com.sksamuel.elastic4s.script.ScriptFieldDefinition
import com.sksamuel.elastic4s.searches.aggs.AbstractAggregation
import com.sksamuel.elastic4s.searches.collapse.CollapseDefinition
import com.sksamuel.elastic4s.searches.queries._
import com.sksamuel.elastic4s.searches.queries.matches.{MatchAllQueryDefinition, MatchQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.term.TermQueryDefinition
import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, SortDefinition}
import com.sksamuel.elastic4s.searches.suggestion.SuggestionDefinition
import com.sksamuel.elastic4s.{FetchSourceContext, IndexesAndTypes}
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.{Duration, FiniteDuration}

// for 2.10 cross build we need to make some parameters out of the case class so its <= 22 parameters
case class Scoring(minScore: Option[Double] = None,
                   rescorers: Seq[RescoreDefinition] = Nil,
                   trackScores: Option[Boolean] = None)

case class Control(pref: Option[String] = None,
                   routing: Option[String] = None,
                   terminateAfter: Option[Int] = None,
                   timeout: Option[Duration] = None)

case class Windowing(from: Option[Int] = None, size: Option[Int] = None, slice: Option[(Int, Int)] = None)

case class Fields(docValues: Seq[String] = Nil,
                  scriptFields: Seq[ScriptFieldDefinition] = Nil,
                  storedFields: Seq[String] = Nil)

case class Suggestions(suggs: Seq[SuggestionDefinition] = Nil, globalSuggestionText: Option[String] = None)

case class Meta(explain: Option[Boolean] = None, stats: Seq[String] = Nil)

case class SearchDefinition(indexesTypes: IndexesAndTypes,
                            aggs: Seq[AbstractAggregation] = Nil,
                            collapse: Option[CollapseDefinition] = None,
                            fields: Fields = Fields(),
                            fetchContext: Option[FetchSourceContext] = None,
                            control: Control = Control(),
                            scoring: Scoring = Scoring(),
                            indicesOptions: Option[IndicesOptions] = None,
                            inners: Seq[InnerHitDefinition] = Nil,
                            indexBoosts: Seq[(String, Double)] = Nil,
                            keepAlive: Option[String] = None,
                            highlight: Option[Highlight] = None,
                            query: Option[QueryDefinition] = None,
                            postFilter: Option[QueryDefinition] = None,
                            requestCache: Option[Boolean] = None,
                            sorts: Seq[SortDefinition] = Nil,
                            suggestions: Suggestions = Suggestions(),
                            windowing: Windowing = Windowing(),
                            meta: Meta = Meta(),
                            searchType: Option[SearchType] = None,
                            searchAfter: Seq[AnyRef] = Nil,
                            version: Option[Boolean] = None,
                            profile: Option[Boolean] = None,
                            source: Option[String] = None,
                            trackHits: Option[Boolean] = None) {

  /** Adds a single string query to this search
    *
    * @param string the query string
    */
  def query(string: String): SearchDefinition = query(QueryStringQueryDefinition(string))

  // adds a query to this search
  def query(q: QueryDefinition): SearchDefinition = copy(query = q.some)

  def minScore(min: Double): SearchDefinition = copy(scoring = scoring.copy(minScore = min.some))

  def types(first: String, rest: String*): SearchDefinition = types(first +: rest)

  def types(types: Iterable[String]): SearchDefinition =
    copy(indexesTypes = IndexesAndTypes(indexesTypes.indexes, types.toSeq))

  def bool(block: => BoolQueryDefinition): SearchDefinition = query(block)

  @deprecated("Use matchAllQuery()", "5.2.0")
  def matchAll(): SearchDefinition = query(new MatchAllQueryDefinition)

  def inner(first: InnerHitDefinition, rest: InnerHitDefinition*): SearchDefinition = inner(first +: rest)

  def inner(inners: Iterable[InnerHitDefinition]): SearchDefinition = copy(inners = inners.toSeq)

  def searchAfter(values: Seq[AnyRef]): SearchDefinition = copy(searchAfter = values)

  def postFilter(block: => QueryDefinition): SearchDefinition = copy(postFilter = block.some)

  def requestCache(requestCache: Boolean): SearchDefinition = copy(requestCache = requestCache.some)

  def aggs(first: AbstractAggregation, rest: AbstractAggregation*): SearchDefinition = aggs(first +: rest)

  def aggs(iterable: Iterable[AbstractAggregation]): SearchDefinition = aggregations(iterable)

  def aggregations(aggs: Iterable[AbstractAggregation]): SearchDefinition = copy(aggs = aggs.toSeq)

  def aggregations(first: AbstractAggregation, rest: AbstractAggregation*): SearchDefinition =
    aggregations(first +: rest)

  @deprecated("use sortBy", "5.0.0")
  def sort(sorts: SortDefinition*): SearchDefinition = sortBy(sorts)

  def sortBy(sorts: SortDefinition*): SearchDefinition = sortBy(sorts)

  def sortBy(sorts: Iterable[SortDefinition]): SearchDefinition = copy(sorts = sorts.toSeq)

  def sortByFieldAsc(name: String): SearchDefinition = sortBy(FieldSortDefinition(name))

  def sortByFieldDesc(name: String): SearchDefinition = sortBy(FieldSortDefinition(name).desc())

  def trackTotalHits(value: Boolean): SearchDefinition = copy(trackHits = Some(value))

  /** This method introduces zero or more script field definitions into the search construction
    *
    * @param fields zero or more [[ScriptFieldDefinition]] instances
    * @return this, an instance of [[SearchDefinition]]
    */
  def scriptfields(fields: ScriptFieldDefinition*): SearchDefinition = scriptfields(fields)

  def scriptfields(_fields: Iterable[ScriptFieldDefinition]): SearchDefinition =
    copy(fields = fields.copy(scriptFields = _fields.toSeq))

  /**
    * Adds a new suggestion to the search request, which can be looked up in the response
    * using the name provided.
    */
  def suggestions(first: SuggestionDefinition, rest: SuggestionDefinition*): SearchDefinition =
    suggestions(first +: rest)

  def suggestions(suggs: Iterable[SuggestionDefinition]): SearchDefinition =
    copy(suggestions = suggestions.copy(suggs = suggs.toSeq))

  def suggestion(sugg: SuggestionDefinition): SearchDefinition = suggestions(Seq(sugg))

  def globalSuggestionText(text: String): SearchDefinition =
    copy(suggestions = suggestions.copy(globalSuggestionText = text.some))

  // Adds a single prefix query to this search
  def prefix(name: String, value: Any): SearchDefinition = query(PrefixQuery(name, value))

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

  /**
    * Sets the source of the request as a json string. Note, if you use this method
    * any other body-level settings will be ignored.
    *
    * HTTP query-parameter settings can still be used, eg limit, routing, search type etc.
    *
    * Unlike rawQuery, source is parsed at the "root" level
    * Query must be valid json beginning with '{' and ending with '}'.
    * Field names must be double quoted.
    *
    * NOTE: This method only works with the HTTP client.
    *
    * Example:
    * {{{
    * search in "*" types("users", "tweets") limit 5 source {
    * """{ "query": { "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } } }"""
    * } searchType SearchType.Scan
    * }}}
    */
  def source(json: String): SearchDefinition = copy(source = json.some)

  def explain(enabled: Boolean): SearchDefinition = copy(meta = meta.copy(explain = enabled.some))

  def highlighting(first: HighlightFieldDefinition, rest: HighlightFieldDefinition*): SearchDefinition =
    highlighting(HighlightOptionsDefinition(), first +: rest)

  def highlighting(fields: Iterable[HighlightFieldDefinition]): SearchDefinition =
    highlighting(HighlightOptionsDefinition(), fields)

  def highlighting(options: HighlightOptionsDefinition,
                   first: HighlightFieldDefinition,
                   rest: HighlightFieldDefinition*): SearchDefinition = highlighting(options, first +: rest)

  def highlighting(options: HighlightOptionsDefinition, fields: Iterable[HighlightFieldDefinition]): SearchDefinition =
    copy(highlight = Highlight(options, fields).some)

  def routing(r: String): SearchDefinition = copy(control = control.copy(routing = r.some))

  def start(i: Int): SearchDefinition = from(i)

  def from(i: Int): SearchDefinition = copy(windowing = windowing.copy(from = i.some))

  def limit(i: Int): SearchDefinition = size(i)

  def size(i: Int): SearchDefinition = copy(windowing = windowing.copy(size = i.some))

  def preference(pref: com.sksamuel.elastic4s.Preference): SearchDefinition = preference(pref.value)

  def preference(pref: String): SearchDefinition = copy(control = control.copy(pref = pref.some))

  def indicesOptions(options: IndicesOptions): SearchDefinition = copy(indicesOptions = options.some)

  def rescore(first: RescoreDefinition, rest: RescoreDefinition*): SearchDefinition = rescore(first +: rest)

  def rescore(rescorers: Iterable[RescoreDefinition]): SearchDefinition =
    copy(scoring = scoring.copy(rescorers = rescorers.toSeq))

  // alias for scroll
  def keepAlive(keepAlive: String): SearchDefinition = scroll(keepAlive)

  def scroll(keepAlive: String): SearchDefinition = copy(keepAlive = keepAlive.some)

  def slice(id: Int, max: Int): SearchDefinition = copy(windowing = windowing.copy(slice = Some(id, max)))

  def searchType(searchType: SearchType): SearchDefinition = copy(searchType = searchType.some)

  def version(version: Boolean): SearchDefinition = copy(version = version.some)

  /**
    * The maximum number of documents to collect for each shard,
    * upon reaching which the query execution will terminate early.
    * If set, the response will have a boolean field terminated_early
    * to indicate whether the query execution has actually terminated
    * early. Defaults to no.
    */
  def terminateAfter(terminateAfter: Int): SearchDefinition =
    copy(control = control.copy(terminateAfter = terminateAfter.some))

  // Allows to return the doc value representation of a field for each hit, for example:
  def docValues(first: String, rest: String*): SearchDefinition = docValues(first +: rest)

  def docValues(_fields: Seq[String]): SearchDefinition = copy(fields = fields.copy(docValues = _fields))

  def indexBoost(map: Map[String, Double]): SearchDefinition = indexBoost(map.toList: _*)

  def indexBoost(tuples: (String, Double)*): SearchDefinition = copy(indexBoosts = tuples)

  def timeout(timeout: FiniteDuration): SearchDefinition = copy(control = control.copy(timeout = timeout.some))

  def stats(groups: String*): SearchDefinition = copy(meta = meta.copy(stats = groups.toSeq))

  def trackScores(enabled: Boolean): SearchDefinition = copy(scoring = scoring.copy(trackScores = enabled.some))

  @deprecated("Renamed to storedFields", "5.0.0")
  def fields(fields: String*): SearchDefinition = storedFields(fields)

  def storedFields(first: String, rest: String*): SearchDefinition = storedFields(first +: rest)

  def storedFields(_fields: Iterable[String]): SearchDefinition =
    copy(fields = fields.copy(storedFields = _fields.toSeq))

  def fetchContext(context: FetchSourceContext): SearchDefinition = copy(fetchContext = context.some)

  def fetchSource(fetch: Boolean): SearchDefinition = copy(fetchContext = FetchSourceContext(fetch).some)

  def sourceInclude(first: String, rest: String*): SearchDefinition = sourceFiltering(first +: rest, Nil)

  def sourceInclude(includes: Iterable[String]): SearchDefinition = sourceFiltering(includes, Nil)

  def sourceExclude(first: String, rest: String*): SearchDefinition = sourceFiltering(Nil, first +: rest)

  def sourceExclude(excludes: Iterable[String]): SearchDefinition = sourceFiltering(Nil, excludes)

  def sourceFiltering(includes: Iterable[String], excludes: Iterable[String]): SearchDefinition =
    copy(fetchContext = FetchSourceContext(true, includes.toArray, excludes.toArray).some)

  def collapse(collapse: CollapseDefinition): SearchDefinition = copy(collapse = collapse.some)
}
