package com.sksamuel.elastic4s.searches

sealed trait SearchType
object SearchType {
  case object DfsQueryThenFetch extends SearchType
  case object QueryThenFetch extends SearchType
}
