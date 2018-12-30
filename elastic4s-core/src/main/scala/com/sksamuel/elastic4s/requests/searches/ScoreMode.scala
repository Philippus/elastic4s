package com.sksamuel.elastic4s.requests.searches

sealed trait ScoreMode
object ScoreMode {

  def valueOf(str: String): ScoreMode = str.toLowerCase match {
    case "avg"   => Avg
    case "max"   => Max
    case "total" => Total
    case "none"  => None
    case "min"   => Min
    case "sum"   => Sum
  }

  case object Avg   extends ScoreMode
  case object Max   extends ScoreMode
  case object Total extends ScoreMode {
    override def toString: String = "sum"
  }
  case object Min   extends ScoreMode
  case object None  extends ScoreMode
  case object Sum   extends ScoreMode
}
