package com.sksamuel.elastic4s.requests.searches

sealed trait QueryRescoreMode

object QueryRescoreMode {

  def valueOf(str: String): QueryRescoreMode = str.toLowerCase match {
    case "avg"      => Avg
    case "max"      => Max
    case "total"    => Total
    case "min"      => Min
    case "multiply" => Multiply
  }

  case object Avg      extends QueryRescoreMode
  case object Max      extends QueryRescoreMode
  case object Min      extends QueryRescoreMode
  case object Total    extends QueryRescoreMode
  case object Multiply extends QueryRescoreMode
}
