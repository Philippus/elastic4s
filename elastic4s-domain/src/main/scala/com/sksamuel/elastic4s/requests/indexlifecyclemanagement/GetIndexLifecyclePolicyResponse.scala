package com.sksamuel.elastic4s.requests.indexlifecyclemanagement

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.JacksonSupport
import com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy.IndexLifecyclePolicy

import scala.collection.JavaConverters._

case class GetIndexLifecyclePolicyResponse(
                                            version: Int,
                                            @JsonProperty("modified_date") modifiedDate: Long,
                                            policy: IndexLifecyclePolicy,
                                            @JsonProperty("in_use_by") inUseBy: Option[InUseBy]
)

object GetIndexLifecyclePolicyResponse {
  def deserialize(node: JsonNode): GetIndexLifecyclePolicyResponse = {
    val policyProperty = node.properties().iterator().next()
    val policyNode     = policyProperty.getValue
    val policyName     = policyProperty.getKey
    val version        = Option(policyNode.get("version")).map(_.asInt(1)).getOrElse(1)
    val modifiedDate   = Option(policyNode.get("modified_date")).map(_.asLong(0L)).getOrElse(0L)
    val policy         = IndexLifecyclePolicy.deserialize(policyNode.get("policy"))
    val inUseBy        = Option(policyNode.get("in_use_by")).map(InUseBy.deserialize)
    GetIndexLifecyclePolicyResponse(version, modifiedDate, policy.copy(name = policyName), inUseBy)
  }

}

case class InUseBy(indices: List[String], data_streams: List[String], composable_templates: List[String])

object InUseBy {
  def deserialize(node: JsonNode): InUseBy = {
    val indices             =
      Option(node.get("indices")).getOrElse(JacksonSupport.mapper.createObjectNode())
        .values().asScala.map(_.asText()).toList
    val dataStreams         =
      Option(node.get("data_streams")).getOrElse(JacksonSupport.mapper.createObjectNode())
        .values().asScala.map(_.asText()).toList
    val composableTemplates =
      Option(node.get("composable_templates")).getOrElse(JacksonSupport.mapper.createObjectNode())
        .values().asScala.map(_.asText()).toList
    InUseBy(indices, dataStreams, composableTemplates)
  }
}
