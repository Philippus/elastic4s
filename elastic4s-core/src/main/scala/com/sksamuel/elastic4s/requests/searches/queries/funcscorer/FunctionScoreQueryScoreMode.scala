package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

sealed trait FunctionScoreQueryScoreMode
object FunctionScoreQueryScoreMode {

  def valueOf(str: String): FunctionScoreQueryScoreMode = str.toLowerCase match {
    case "avg"      => Avg
    case "first"    => First
    case "max"      => Max
    case "min"      => Min
    case "multiply" => Multiply
    case "sum"      => Sum
  }

  case object First    extends FunctionScoreQueryScoreMode
  case object Avg      extends FunctionScoreQueryScoreMode
  case object Max      extends FunctionScoreQueryScoreMode
  case object Multiply extends FunctionScoreQueryScoreMode
  case object Min      extends FunctionScoreQueryScoreMode
  case object Sum      extends FunctionScoreQueryScoreMode
}
