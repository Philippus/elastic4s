package com.sksamuel.elastic4s.handlers.indexlifecyclemanagement

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicyAction

object ElasticPolicyActionContentBuilder {
  def apply(action: IndexLifecyclePolicyAction): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    action.settings.foreach { case (name, value) => builder.field(name, value) }
    builder
  }
}
