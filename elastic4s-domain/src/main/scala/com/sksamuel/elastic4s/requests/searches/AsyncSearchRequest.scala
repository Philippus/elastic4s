package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.admin.IndicesOptionsRequest
import com.sksamuel.elastic4s.requests.common.{FetchSourceContext, Preference}
import com.sksamuel.elastic4s.requests.script.ScriptField
import com.sksamuel.elastic4s.requests.searches.aggs.AbstractAggregation
import com.sksamuel.elastic4s.requests.searches.collapse.CollapseRequest
import com.sksamuel.elastic4s.requests.searches.queries._
import com.sksamuel.elastic4s.requests.searches.queries.compound.BoolQuery
import com.sksamuel.elastic4s.requests.searches.queries.matches.{MatchAllQuery, MatchQuery}
import com.sksamuel.elastic4s.requests.searches.sort.{FieldSort, Sort}
import com.sksamuel.elastic4s.requests.searches.suggestion.Suggestion
import com.sksamuel.elastic4s.requests.searches.term.TermQuery
import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

import scala.concurrent.duration.{Duration, FiniteDuration}

case class AsyncSearchRequest(indexes: Indexes,
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
                              seqNoPrimaryTerm: Option[Boolean] = None,
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
  def query(string: String): AsyncSearchRequest = query(QueryStringQuery(string))

  // adds a query to this search
  def query(q: Query): AsyncSearchRequest = copy(query = q.some)

  def minScore(min: Double): AsyncSearchRequest = copy(minScore = Some(min))

  def bool(block: => BoolQuery): AsyncSearchRequest = query(block)

  def inner(first: InnerHit, rest: InnerHit*): AsyncSearchRequest = inner(first +: rest)

  def inner(inners: Iterable[InnerHit]): AsyncSearchRequest = copy(inners = inners.toSeq)

  def searchAfter(values: Seq[Any]): AsyncSearchRequest = copy(searchAfter = values)

  def postFilter(block: => Query): AsyncSearchRequest = copy(postFilter = block.some)

  def requestCache(requestCache: Boolean): AsyncSearchRequest = copy(requestCache = requestCache.some)

  def aggs(first: AbstractAggregation, rest: AbstractAggregation*): AsyncSearchRequest = aggs(first +: rest)

  def aggs(iterable: Iterable[AbstractAggregation]): AsyncSearchRequest = aggregations(iterable)

  def aggregations(aggs: Iterable[AbstractAggregation]): AsyncSearchRequest = copy(aggs = aggs.toSeq)

  def aggregations(first: AbstractAggregation, rest: AbstractAggregation*): AsyncSearchRequest =
    aggregations(first +: rest)

  def sortBy(sorts: Sort*): AsyncSearchRequest = sortBy(sorts)

  def sortBy(sorts: Iterable[Sort]): AsyncSearchRequest = copy(sorts = sorts.toSeq)

  def sortByFieldAsc(name: String): AsyncSearchRequest = sortBy(FieldSort(name))

  def sortByFieldDesc(name: String): AsyncSearchRequest = sortBy(FieldSort(name).desc())

  def trackTotalHits(value: Boolean): AsyncSearchRequest = copy(trackHits = Some(value))

  /** This method introduces zero or more script field definitions into the search construction
    *
    * @param fields zero or more [[ScriptField]] instances
    * @return this, an instance of [[AsyncSearchRequest]]
    */
  def scriptfields(fields: ScriptField*): AsyncSearchRequest = scriptfields(fields)

  def scriptfields(_fields: Iterable[ScriptField]): AsyncSearchRequest =
    copy(scriptFields = _fields.toSeq)

  /**
    * Adds a new suggestion to the search request, which can be looked up in the response
    * using the name provided.
    */
  def suggestions(first: Suggestion, rest: Suggestion*): AsyncSearchRequest =
    suggestions(first +: rest)

  def suggestions(suggs: Iterable[Suggestion]): AsyncSearchRequest =
    copy(suggs = suggs.toSeq)

  def suggestion(sugg: Suggestion): AsyncSearchRequest = suggestions(Seq(sugg))

  def globalSuggestionText(text: String): AsyncSearchRequest =
    copy(globalSuggestionText = text.some)

  // Adds a single prefix query to this search
  def prefix(name: String, value: Any): AsyncSearchRequest = query(PrefixQuery(name, value))

  def regexQuery(tuple: (String, String)): AsyncSearchRequest = regexQuery(tuple._1, tuple._2)

  // Adds a single regex query to this search
  def regexQuery(field: String, value: String): AsyncSearchRequest = query(RegexQuery(field, value))

  def termQuery(tuple: (String, Any)): AsyncSearchRequest = termQuery(tuple._1, tuple._2)

  def termQuery(field: String, value: Any): AsyncSearchRequest = {
    val q = TermQuery(field, value)
    query(q)
  }

  def matchQuery(field: String, value: Any): AsyncSearchRequest = {
    val q = MatchQuery(field, value)
    query(q)
  }

  def matchAllQuery(): AsyncSearchRequest = query(MatchAllQuery())

  /** Expects a query in json format and sets the query of the search request.
    * i.e. underneath a "query" field if referencing HTTP API
    * Query must be valid json beginning with '{' and ending with '}'.
    * Field names must be double quoted.
    *
    * Example:
    * {{{
    * search in "*" limit 5 rawQuery {
    * """{ "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } }"""
    * } searchType SearchType.Scan
    * }}}
    */
  def rawQuery(json: String): AsyncSearchRequest = query(RawQuery(json))

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
    * search in "*" limit 5 source {
    * """{ "query": { "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } } }"""
    * } searchType SearchType.Scan
    * }}}
    */
  def source(json: String): AsyncSearchRequest = copy(source = json.some)

  def explain(enabled: Boolean): AsyncSearchRequest = copy(explain = enabled.some)

  def highlighting(first: HighlightField, rest: HighlightField*): AsyncSearchRequest =
    highlighting(HighlightOptions(), first +: rest)

  def highlighting(fields: Iterable[HighlightField]): AsyncSearchRequest =
    highlighting(HighlightOptions(), fields)

  def highlighting(options: HighlightOptions, first: HighlightField, rest: HighlightField*): AsyncSearchRequest =
    highlighting(options, first +: rest)

  def highlighting(options: HighlightOptions, fields: Iterable[HighlightField]): AsyncSearchRequest =
    copy(highlight = Highlight(options, fields).some)

  def routing(r: String): AsyncSearchRequest = copy(routing = r.some)

  def start(i: Int): AsyncSearchRequest = from(i)

  def from(i: Int): AsyncSearchRequest = copy(from = i.some)

  def limit(i: Int): AsyncSearchRequest = size(i)

  def size(i: Int): AsyncSearchRequest = copy(size = i.some)

  def preference(pref: Preference): AsyncSearchRequest = preference(pref.value)

  def preference(pref: String): AsyncSearchRequest = copy(pref = pref.some)

  def indicesOptions(options: IndicesOptionsRequest): AsyncSearchRequest = copy(indicesOptions = options.some)

  def rescore(first: Rescore, rest: Rescore*): AsyncSearchRequest = rescore(first +: rest)

  def rescore(rescorers: Iterable[Rescore]): AsyncSearchRequest =
    copy(rescorers = rescorers.toSeq)

  // alias for scroll
  def keepAlive(keepAlive: String): AsyncSearchRequest = scroll(keepAlive)

  def keepAlive(duration: FiniteDuration): AsyncSearchRequest = scroll(duration)

  def scroll(keepAlive: String): AsyncSearchRequest = copy(keepAlive = keepAlive.some)

  def scroll(duration: FiniteDuration): AsyncSearchRequest = copy(keepAlive = Some(duration.toSeconds + "s"))

  def slice(id: Int, max: Int): AsyncSearchRequest = copy(slice = Some(id, max))

  def searchType(searchType: SearchType): AsyncSearchRequest = copy(searchType = searchType.some)

  def version(version: Boolean): AsyncSearchRequest = copy(version = version.some)

  def seqNoPrimaryTerm(seqNoPrimaryTerm: Boolean): AsyncSearchRequest = copy(seqNoPrimaryTerm = seqNoPrimaryTerm.some)

  /**
    * The maximum number of documents to collect for each shard,
    * upon reaching which the query execution will terminate early.
    * If set, the response will have a boolean field terminated_early
    * to indicate whether the query execution has actually terminated
    * early. Defaults to no.
    */
  def terminateAfter(terminateAfter: Int): AsyncSearchRequest =
    copy(terminateAfter = terminateAfter.some)

  // Allows to return the doc value representation of a field for each hit, for example:
  def docValues(first: String, rest: String*): AsyncSearchRequest = docValues(first +: rest)

  def docValues(_fields: Seq[String]): AsyncSearchRequest = copy(docValues = _fields)

  def indexBoost(map: Map[String, Double]): AsyncSearchRequest = indexBoost(map.toList: _*)

  def indexBoost(tuples: (String, Double)*): AsyncSearchRequest = copy(indexBoosts = tuples)

  def timeout(timeout: FiniteDuration): AsyncSearchRequest = copy(timeout = timeout.some)

  def stats(groups: String*): AsyncSearchRequest = copy(stats = groups.toSeq)

  def trackScores(enabled: Boolean): AsyncSearchRequest = copy(trackScores = enabled.some)

  def storedFields(first: String, rest: String*): AsyncSearchRequest = storedFields(first +: rest)

  def storedFields(_fields: Iterable[String]): AsyncSearchRequest =
    copy(storedFields = _fields.toSeq)

  def fetchContext(context: FetchSourceContext): AsyncSearchRequest = copy(fetchContext = context.some)

  def fetchSource(fetch: Boolean): AsyncSearchRequest = copy(fetchContext = FetchSourceContext(fetch).some)

  def sourceInclude(first: String, rest: String*): AsyncSearchRequest = sourceFiltering(first +: rest, Nil)

  def sourceInclude(includes: Iterable[String]): AsyncSearchRequest = sourceFiltering(includes, Nil)

  def sourceExclude(first: String, rest: String*): AsyncSearchRequest = sourceFiltering(Nil, first +: rest)

  def sourceExclude(excludes: Iterable[String]): AsyncSearchRequest = sourceFiltering(Nil, excludes)

  def sourceFiltering(includes: Iterable[String], excludes: Iterable[String]): AsyncSearchRequest =
    copy(fetchContext = FetchSourceContext(true, includes.toArray, excludes.toArray).some)

  def collapse(collapse: CollapseRequest): AsyncSearchRequest = copy(collapse = collapse.some)

  def typedKeys(enabled: Boolean): AsyncSearchRequest = copy(typedKeys = enabled.some)
}
