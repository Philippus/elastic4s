package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

sealed trait MultiValueMode
object MultiValueMode {

  def valueOf(str: String): MultiValueMode = str.toLowerCase match {
    case "avg"    => Avg
    case "max"    => Max
    case "min"    => Min
    case "median" => Median
    case "sum"    => Sum
  }

  case object Avg    extends MultiValueMode
  case object Min    extends MultiValueMode
  case object Max    extends MultiValueMode
  case object Sum    extends MultiValueMode
  case object Median extends MultiValueMode
}
