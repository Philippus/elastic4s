package com.sksamuel.elastic4s.requests.searches

sealed trait SearchType
object SearchType {

  case object DfsQueryThenFetch extends SearchType
  case object QueryThenFetch    extends SearchType

  val DEFAULT              = DfsQueryThenFetch
  val QUERY_THEN_FETCH     = QueryThenFetch
  val DFS_QUERY_THEN_FETCH = DfsQueryThenFetch
}
