package com.sksamuel.elastic4s.searches

sealed trait ScoreMode
object ScoreMode {

  def valueOf(str: String): ScoreMode = str.toLowerCase match {
    case "avg" => Avg
    case "max" => Max
    case "total" => Total
    case "none" => None
    case "min" => Min
  }

  /**
    * Parent hit's score is the average of all child scores.
    */
  case object Avg extends ScoreMode
  /**
    * Parent hit's score is the max of all child scores.
    */
  case object Max extends ScoreMode
  /**
    * Parent hit's score is the sum of all child scores.
    */
  case object Total extends ScoreMode
  /**
    * Parent hit's score is the min of all child scores.
    */
  case object Min extends ScoreMode

  case object None extends ScoreMode
}
