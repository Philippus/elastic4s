package com.sksamuel.elastic4s.requests.common

sealed trait RefreshPolicy

object RefreshPolicy {

  def valueOf(str: String): RefreshPolicy = str.toLowerCase match {
    case "immediate" => RefreshPolicy.Immediate
    case "none"      => RefreshPolicy.None
    case _           => RefreshPolicy.WaitFor
  }

  case object None      extends RefreshPolicy
  case object Immediate extends RefreshPolicy
  case object WaitFor   extends RefreshPolicy

  val IMMEDIATE: Immediate.type = Immediate
  @deprecated("Use WAIT_FOR", "7.0")
  val WAIT_UNTIL: WaitFor.type = WaitFor
  val WAIT_FOR: WaitFor.type = WaitFor
  val NONE: None.type = None
}
