package com.sksamuel.elastic4s.requests.searches.sort

sealed trait SortMode
object SortMode {

  def valueOf(str: String): SortMode = str.toLowerCase match {
    case "avg"    => Avg
    case "max"    => Max
    case "min"    => Min
    case "median" => Median
    case "sum"    => Sum
  }

  case object Avg    extends SortMode
  case object Median extends SortMode
  case object Min    extends SortMode
  case object Max    extends SortMode
  case object Sum    extends SortMode

  def AVG    = Avg
  def MEDIAN = Median
  def MIN    = Min
  def MAX    = Max
  def SUM    = Sum
}
