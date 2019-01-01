package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.requests.admin.IndicesOptionsRequest
import com.sksamuel.elastic4s.requests.common.{FetchSourceContext, Preference}
import com.sksamuel.elastic4s.requests.script.ScriptField
import com.sksamuel.elastic4s.requests.searches.aggs.AbstractAggregation
import com.sksamuel.elastic4s.requests.searches.collapse.CollapseRequest
import com.sksamuel.elastic4s.requests.searches.queries._
import com.sksamuel.elastic4s.requests.searches.queries.matches.{MatchAllQuery, MatchQuery}
import com.sksamuel.elastic4s.requests.searches.queries.term.TermQuery
import com.sksamuel.elastic4s.requests.searches.sort.{FieldSort, Sort}
import com.sksamuel.elastic4s.requests.searches.suggestion.Suggestion
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.{Duration, FiniteDuration}

case class SearchRequest(indexesTypes: IndexesAndTypes,
                         aggs: Seq[AbstractAggregation] = Nil,
                         collapse: Option[CollapseRequest] = None,
                         docValues: Seq[String] = Nil,
                         scriptFields: Seq[ScriptField] = Nil,
                         storedFields: Seq[String] = Nil,
                         fetchContext: Option[FetchSourceContext] = None,
                         pref: Option[String] = None,
                         routing: Option[String] = None,
                         terminateAfter: Option[Int] = None,
                         timeout: Option[Duration] = None,
                         minScore: Option[Double] = None,
                         rescorers: Seq[Rescore] = Nil,
                         trackScores: Option[Boolean] = None,
                         indicesOptions: Option[IndicesOptionsRequest] = None,
                         inners: Seq[InnerHit] = Nil,
                         indexBoosts: Seq[(String, Double)] = Nil,
                         keepAlive: Option[String] = None,
                         highlight: Option[Highlight] = None,
                         query: Option[Query] = None,
                         postFilter: Option[Query] = None,
                         requestCache: Option[Boolean] = None,
                         sorts: Seq[Sort] = Nil,
                         suggs: Seq[Suggestion] = Nil,
                         globalSuggestionText: Option[String] = None,
                         from: Option[Int] = None,
                         size: Option[Int] = None,
                         slice: Option[(Int, Int)] = None,
                         explain: Option[Boolean] = None,
                         stats: Seq[String] = Nil,
                         searchType: Option[SearchType] = None,
                         searchAfter: Seq[Any] = Nil,
                         version: Option[Boolean] = None,
                         profile: Option[Boolean] = None,
                         source: Option[String] = None,
                         trackHits: Option[Boolean] = None,
                         allowPartialSearchResults: Option[Boolean] = None,
                         batchedReduceSize: Option[Int] = None,
                         typedKeys: Option[Boolean] = None) {

  /** Adds a single string query to this search
    *
    * @param string the query string
    */
  def query(string: String): SearchRequest = query(QueryStringQuery(string))

  // adds a query to this search
  def query(q: Query): SearchRequest = copy(query = q.some)

  def minScore(min: Double): SearchRequest = copy(minScore = Some(min))

  def types(first: String, rest: String*): SearchRequest = types(first +: rest)

  def types(types: Iterable[String]): SearchRequest =
    copy(indexesTypes = IndexesAndTypes(indexesTypes.indexes, types.toSeq))

  def bool(block: => BoolQuery): SearchRequest = query(block)

  @deprecated("Use matchAllQuery()", "5.2.0")
  def matchAll(): SearchRequest = query(new MatchAllQuery)

  def inner(first: InnerHit, rest: InnerHit*): SearchRequest = inner(first +: rest)

  def inner(inners: Iterable[InnerHit]): SearchRequest = copy(inners = inners.toSeq)

  def searchAfter(values: Seq[Any]): SearchRequest = copy(searchAfter = values)

  def postFilter(block: => Query): SearchRequest = copy(postFilter = block.some)

  def requestCache(requestCache: Boolean): SearchRequest = copy(requestCache = requestCache.some)

  def aggs(first: AbstractAggregation, rest: AbstractAggregation*): SearchRequest = aggs(first +: rest)

  def aggs(iterable: Iterable[AbstractAggregation]): SearchRequest = aggregations(iterable)

  def aggregations(aggs: Iterable[AbstractAggregation]): SearchRequest = copy(aggs = aggs.toSeq)

  def aggregations(first: AbstractAggregation, rest: AbstractAggregation*): SearchRequest =
    aggregations(first +: rest)

  @deprecated("use sortBy", "5.0.0")
  def sort(sorts: Sort*): SearchRequest = sortBy(sorts)

  def sortBy(sorts: Sort*): SearchRequest = sortBy(sorts)

  def sortBy(sorts: Iterable[Sort]): SearchRequest = copy(sorts = sorts.toSeq)

  def sortByFieldAsc(name: String): SearchRequest = sortBy(FieldSort(name))

  def sortByFieldDesc(name: String): SearchRequest = sortBy(FieldSort(name).desc())

  def trackTotalHits(value: Boolean): SearchRequest = copy(trackHits = Some(value))

  /** This method introduces zero or more script field definitions into the search construction
    *
    * @param fields zero or more [[ScriptField]] instances
    * @return this, an instance of [[SearchRequest]]
    */
  def scriptfields(fields: ScriptField*): SearchRequest = scriptfields(fields)

  def scriptfields(_fields: Iterable[ScriptField]): SearchRequest =
    copy(scriptFields = _fields.toSeq)

  /**
    * Adds a new suggestion to the search request, which can be looked up in the response
    * using the name provided.
    */
  def suggestions(first: Suggestion, rest: Suggestion*): SearchRequest =
    suggestions(first +: rest)

  def suggestions(suggs: Iterable[Suggestion]): SearchRequest =
    copy(suggs = suggs.toSeq)

  def suggestion(sugg: Suggestion): SearchRequest = suggestions(Seq(sugg))

  def globalSuggestionText(text: String): SearchRequest =
    copy(globalSuggestionText = text.some)

  // Adds a single prefix query to this search
  def prefix(name: String, value: Any): SearchRequest = query(PrefixQuery(name, value))

  @deprecated("use regexQuery(...)", "5.0.0")
  def regex(tuple: (String, String)): SearchRequest = regexQuery(tuple)

  def regexQuery(tuple: (String, String)): SearchRequest = regexQuery(tuple._1, tuple._2)

  // Adds a single regex query to this search
  def regexQuery(field: String, value: String): SearchRequest = query(RegexQuery(field, value))

  @deprecated("use termQuery()", "5.0.0")
  def term(tuple: (String, Any)): SearchRequest = termQuery(tuple)

  @deprecated("use termQuery()", "5.0.0")
  def term(field: String, value: Any): SearchRequest = termQuery(field, value)

  def termQuery(tuple: (String, Any)): SearchRequest = termQuery(tuple._1, tuple._2)

  def termQuery(field: String, value: Any): SearchRequest = {
    val q = TermQuery(field, value)
    query(q)
  }

  def matchQuery(field: String, value: Any): SearchRequest = {
    val q = MatchQuery(field, value)
    query(q)
  }

  def matchAllQuery(): SearchRequest = query(MatchAllQuery())

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
  def rawQuery(json: String): SearchRequest = query(RawQuery(json))

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
  def source(json: String): SearchRequest = copy(source = json.some)

  def explain(enabled: Boolean): SearchRequest = copy(explain = enabled.some)

  def highlighting(first: HighlightField, rest: HighlightField*): SearchRequest =
    highlighting(HighlightOptions(), first +: rest)

  def highlighting(fields: Iterable[HighlightField]): SearchRequest =
    highlighting(HighlightOptions(), fields)

  def highlighting(options: HighlightOptions, first: HighlightField, rest: HighlightField*): SearchRequest =
    highlighting(options, first +: rest)

  def highlighting(options: HighlightOptions, fields: Iterable[HighlightField]): SearchRequest =
    copy(highlight = Highlight(options, fields).some)

  def routing(r: String): SearchRequest = copy(routing = r.some)

  def start(i: Int): SearchRequest = from(i)

  def from(i: Int): SearchRequest = copy(from = i.some)

  def limit(i: Int): SearchRequest = size(i)

  def size(i: Int): SearchRequest = copy(size = i.some)

  def preference(pref: Preference): SearchRequest = preference(pref.value)

  def preference(pref: String): SearchRequest = copy(pref = pref.some)

  def indicesOptions(options: IndicesOptionsRequest): SearchRequest = copy(indicesOptions = options.some)

  def rescore(first: Rescore, rest: Rescore*): SearchRequest = rescore(first +: rest)

  def rescore(rescorers: Iterable[Rescore]): SearchRequest =
    copy(rescorers = rescorers.toSeq)

  // alias for scroll
  def keepAlive(keepAlive: String): SearchRequest = scroll(keepAlive)
  def keepAlive(duration: FiniteDuration): SearchRequest = scroll(duration)

  def scroll(keepAlive: String): SearchRequest = copy(keepAlive = keepAlive.some)
  def scroll(duration: FiniteDuration): SearchRequest = copy(keepAlive = Some(duration.toSeconds + "s"))

  def slice(id: Int, max: Int): SearchRequest = copy(slice = Some(id, max))

  def searchType(searchType: SearchType): SearchRequest = copy(searchType = searchType.some)

  def version(version: Boolean): SearchRequest = copy(version = version.some)

  /**
    * The maximum number of documents to collect for each shard,
    * upon reaching which the query execution will terminate early.
    * If set, the response will have a boolean field terminated_early
    * to indicate whether the query execution has actually terminated
    * early. Defaults to no.
    */
  def terminateAfter(terminateAfter: Int): SearchRequest =
    copy(terminateAfter = terminateAfter.some)

  // Allows to return the doc value representation of a field for each hit, for example:
  def docValues(first: String, rest: String*): SearchRequest = docValues(first +: rest)

  def docValues(_fields: Seq[String]): SearchRequest = copy(docValues = _fields)

  def indexBoost(map: Map[String, Double]): SearchRequest = indexBoost(map.toList: _*)

  def indexBoost(tuples: (String, Double)*): SearchRequest = copy(indexBoosts = tuples)

  def timeout(timeout: FiniteDuration): SearchRequest = copy(timeout = timeout.some)

  def stats(groups: String*): SearchRequest = copy(stats = groups.toSeq)

  def trackScores(enabled: Boolean): SearchRequest = copy(trackScores = enabled.some)

  @deprecated("Renamed to storedFields", "5.0.0")
  def fields(fields: String*): SearchRequest = storedFields(fields)

  def storedFields(first: String, rest: String*): SearchRequest = storedFields(first +: rest)

  def storedFields(_fields: Iterable[String]): SearchRequest =
    copy(storedFields = _fields.toSeq)

  def fetchContext(context: FetchSourceContext): SearchRequest = copy(fetchContext = context.some)

  def fetchSource(fetch: Boolean): SearchRequest = copy(fetchContext = FetchSourceContext(fetch).some)

  def sourceInclude(first: String, rest: String*): SearchRequest = sourceFiltering(first +: rest, Nil)

  def sourceInclude(includes: Iterable[String]): SearchRequest = sourceFiltering(includes, Nil)

  def sourceExclude(first: String, rest: String*): SearchRequest = sourceFiltering(Nil, first +: rest)

  def sourceExclude(excludes: Iterable[String]): SearchRequest = sourceFiltering(Nil, excludes)

  def sourceFiltering(includes: Iterable[String], excludes: Iterable[String]): SearchRequest =
    copy(fetchContext = FetchSourceContext(true, includes.toArray, excludes.toArray).some)

  def collapse(collapse: CollapseRequest): SearchRequest = copy(collapse = collapse.some)

  def typedKeys(enabled: Boolean): SearchRequest = copy(typedKeys = enabled.some)
}
