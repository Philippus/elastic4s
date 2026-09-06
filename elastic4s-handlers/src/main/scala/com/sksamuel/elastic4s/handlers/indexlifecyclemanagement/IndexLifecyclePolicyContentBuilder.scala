package com.sksamuel.elastic4s.handlers.indexlifecyclemanagement

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicy

object IndexLifecyclePolicyContentBuilder {
  def apply(policy: IndexLifecyclePolicy): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("policy")
    if (policy.meta.nonEmpty) {
      builder.startObject("_meta")
      policy.meta.foreach { case (k, v) => builder.field(k, v) }
      builder.endObject()
    }
    builder.startObject("phases")
    policy.phases.foreach(phase => builder.rawField(phase.phaseName, ElasticPolicyPhaseContentBuilder(phase)))
    builder.endObject()
    builder.endObject()
  }
}
