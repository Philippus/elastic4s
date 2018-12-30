package com.sksamuel.elastic4s.requests.common

object RefreshPolicyHttpValue {
  def apply(policy: RefreshPolicy): String = policy match {
    case RefreshPolicy.Immediate => "true"
    case RefreshPolicy.WaitFor   => "wait_for"
    case RefreshPolicy.None      => "false"
  }
}
