package com.sksamuel.elastic4s.requests.searches.sort

sealed trait SortOrder
object SortOrder {
  case object Asc  extends SortOrder
  case object Desc extends SortOrder

  def DESC = Desc
  def ASC  = Asc
}
