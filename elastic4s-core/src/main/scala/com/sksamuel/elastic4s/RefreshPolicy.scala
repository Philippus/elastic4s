package com.sksamuel.elastic4s

sealed trait RefreshPolicy

object RefreshPolicy {

  def valueOf(str: String): RefreshPolicy = str.toLowerCase match {
    case "immediate" => RefreshPolicy.Immediate
    case "none" => RefreshPolicy.None
    case _ => RefreshPolicy.WaitFor
  }

  case object None extends RefreshPolicy
  case object Immediate extends RefreshPolicy
  case object WaitFor extends RefreshPolicy

  @deprecated("use Immediate", "6.0.0")
  def IMMEDIATE = Immediate

  @deprecated("use WaitFor", "6.0.0")
  def WAIT_UNTIL = WaitFor

  @deprecated("use None", "6.0.0")
  def NONE = None
}
