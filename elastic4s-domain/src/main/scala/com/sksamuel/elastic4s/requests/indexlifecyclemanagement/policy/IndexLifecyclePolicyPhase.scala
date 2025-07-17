package com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.JacksonSupport
import com.sksamuel.elastic4s.json.{JsonValue, StringValue}

import scala.collection.JavaConverters._

case class IndexLifecyclePolicyPhase(
    phaseName: String,
    settings: Map[String, JsonValue],
    actions: List[IndexLifecyclePolicyAction]
) {
  def withSettings(settings: (String, JsonValue)*): IndexLifecyclePolicyPhase =
    copy(settings = this.settings ++ settings.toMap)

  def withActions(actions: IndexLifecyclePolicyAction*): IndexLifecyclePolicyPhase =
    copy(actions = actions.toList ::: this.actions)
}

object IndexLifecyclePolicyPhase {
  def apply(phaseName: String): IndexLifecyclePolicyPhase = IndexLifecyclePolicyPhase(phaseName, Map.empty, Nil)

  def deserialize(name: String, node: JsonNode): IndexLifecyclePolicyPhase = {
    val actions = Option(node.get("actions"))
      .getOrElse(JacksonSupport.mapper.createObjectNode())
      .properties()
      .asScala
      .map(entry => IndexLifecyclePolicyAction.deserialize(entry.getKey, entry.getValue))
      .toList

    val settings = node.properties().asScala.filterNot(_.getKey == "actions").map { entry =>
      entry.getKey -> StringValue(entry.getValue.asText())
    }.toMap

    IndexLifecyclePolicyPhase(name, settings, actions)
  }

  val DeletePhase: IndexLifecyclePolicyPhase = IndexLifecyclePolicyPhase("delete")
  val WarmPhase: IndexLifecyclePolicyPhase   = IndexLifecyclePolicyPhase("warm")
  val ColdPhase: IndexLifecyclePolicyPhase   = IndexLifecyclePolicyPhase("cold")
  val FrozenPhase: IndexLifecyclePolicyPhase = IndexLifecyclePolicyPhase("frozen")
  val HotPhase: IndexLifecyclePolicyPhase    = IndexLifecyclePolicyPhase("hot")
}
