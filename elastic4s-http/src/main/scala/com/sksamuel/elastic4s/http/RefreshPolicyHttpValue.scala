package com.sksamuel.elastic4s.http

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy

object RefreshPolicyHttpValue {
  def apply(policy: RefreshPolicy): String = policy match {
    case RefreshPolicy.IMMEDIATE => "true"
    case RefreshPolicy.WAIT_UNTIL => "wait_until"
    case RefreshPolicy.NONE => "false"
  }
}
