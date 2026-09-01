package com.sksamuel.elastic4s.handlers.indexlifecyclemanagement

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicyPhase

object ElasticPolicyPhaseContentBuilder {
  def apply(phase: IndexLifecyclePolicyPhase): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    phase.settings.foreach { case (name, value) => builder.field(name, value) }
    builder.startObject("actions")
    phase.actions.map(action => builder.rawField(action.actionName, ElasticPolicyActionContentBuilder(action)))
    builder.endObject()
    builder.endObject()
  }
}
