package com.sksamuel.elastic4s.searches

sealed trait SearchType
object SearchType {

  case object DfsQueryThenFetch extends SearchType
  case object QueryThenFetch extends SearchType

  def DEFAULT = DfsQueryThenFetch
  def QUERY_THEN_FETCH = QueryThenFetch
  def DFS_QUERY_THEN_FETCH = DfsQueryThenFetch
}
