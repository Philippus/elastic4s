package com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.JacksonSupport
import com.sksamuel.elastic4s.json.{JsonValue, StringValue}

import scala.collection.JavaConverters._

case class IndexLifecyclePolicy(
    name: String,
    phases: List[IndexLifecyclePolicyPhase],
    meta: Map[String, JsonValue]
) {
  def withPhases(addPhases: IndexLifecyclePolicyPhase*): IndexLifecyclePolicy =
    copy(phases = addPhases.toList ::: phases)
  def withMeta(meta: (String, JsonValue)*): IndexLifecyclePolicy              =
    copy(meta = this.meta ++ meta.toMap)
}

object IndexLifecyclePolicy {
  def apply(name: String): IndexLifecyclePolicy = IndexLifecyclePolicy(name, Nil, Map.empty)

  def deserialize(node: JsonNode): IndexLifecyclePolicy = {

    val phases = Option(node.get("phases"))
      .getOrElse(JacksonSupport.mapper.createObjectNode())
      .properties()
      .asScala
      .map(entry => IndexLifecyclePolicyPhase.deserialize(entry.getKey, entry.getValue))
      .toList

    val meta = Option(node.get("_meta"))
      .getOrElse(JacksonSupport.mapper.createObjectNode())
      .properties()
      .asScala
      .map(entry => entry.getKey -> StringValue(entry.getValue.asText()))
      .toMap

    IndexLifecyclePolicy("", phases, meta)
  }
}
