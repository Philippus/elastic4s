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

  def IMMEDIATE: Immediate.type = Immediate
  def WAIT_UNTIL: WaitFor.type = WaitFor
  def NONE: None.type = None
}
