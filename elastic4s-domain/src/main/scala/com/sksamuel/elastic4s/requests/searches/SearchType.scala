package com.sksamuel.elastic4s.requests.searches

sealed trait SearchType
object SearchType {

  case object DfsQueryThenFetch extends SearchType
  case object QueryThenFetch    extends SearchType

  val DEFAULT: QueryThenFetch.type = QueryThenFetch
  val QUERY_THEN_FETCH: QueryThenFetch.type = QueryThenFetch
  val DFS_QUERY_THEN_FETCH: DfsQueryThenFetch.type = DfsQueryThenFetch
}
