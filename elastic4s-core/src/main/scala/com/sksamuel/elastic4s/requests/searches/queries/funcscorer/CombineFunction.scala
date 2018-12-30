package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

sealed trait CombineFunction
object CombineFunction {

  def valueOf(str: String): CombineFunction = str.toLowerCase match {
    case "avg"      => Avg
    case "max"      => Max
    case "min"      => Min
    case "replace"  => Replace
    case "sum"      => Sum
    case "multiply" => Multiply
  }

  case object Avg      extends CombineFunction
  case object Min      extends CombineFunction
  case object Max      extends CombineFunction
  case object Sum      extends CombineFunction
  case object Multiply extends CombineFunction
  case object Replace  extends CombineFunction
}
