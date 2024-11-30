package com.sksamuel.elastic4s.requests.searches.sort

sealed trait SortOrder
object SortOrder {
  case object Asc  extends SortOrder
  case object Desc extends SortOrder

  val DESC: SortOrder = Desc
  val ASC: SortOrder  = Asc
}
