package com.sksamuel.elastic4s.requests.indexlifecyclemanagement.policy

import com.fasterxml.jackson.databind.JsonNode
import com.sksamuel.elastic4s.json.{JsonValue, StringValue}

import scala.collection.JavaConverters._

case class IndexLifecyclePolicyAction(
    actionName: String,
    settings: Map[String, JsonValue]
) {

  def withSettings(settings: (String, JsonValue)*): IndexLifecyclePolicyAction =
    copy(settings = this.settings ++ settings.toMap)
}

object IndexLifecyclePolicyAction {
  def apply(actionName: String): IndexLifecyclePolicyAction = IndexLifecyclePolicyAction(actionName, Map.empty)

  def deserialize(actionName: String, node: JsonNode): IndexLifecyclePolicyAction = {
    val settings = node.properties().asScala.map { entry =>
      entry.getKey -> StringValue(entry.getValue.asText())
    }.toMap

    IndexLifecyclePolicyAction(actionName, settings)
  }

  val ForceMergeAction: IndexLifecyclePolicyAction         = IndexLifecyclePolicyAction("forcemerge")
  val DeleteAction: IndexLifecyclePolicyAction             = IndexLifecyclePolicyAction("delete")
  val AllocateAction: IndexLifecyclePolicyAction           = IndexLifecyclePolicyAction("allocate")
  val DownsampleAction: IndexLifecyclePolicyAction         = IndexLifecyclePolicyAction("downsample")
  val FreezeAction: IndexLifecyclePolicyAction             = IndexLifecyclePolicyAction("freeze")
  val MigrateAction: IndexLifecyclePolicyAction            = IndexLifecyclePolicyAction("migrate")
  val ReadonlyAction: IndexLifecyclePolicyAction           = IndexLifecyclePolicyAction("readonly")
  val RolloverAction: IndexLifecyclePolicyAction           = IndexLifecyclePolicyAction("rollover")
  val SetPriorityAction: IndexLifecyclePolicyAction        = IndexLifecyclePolicyAction("set_priority")
  val SearchableSnapshotAction: IndexLifecyclePolicyAction = IndexLifecyclePolicyAction("searchable_snapshot")
  val ShrinkAction: IndexLifecyclePolicyAction             = IndexLifecyclePolicyAction("shrink")
  val UnfollowAction: IndexLifecyclePolicyAction           = IndexLifecyclePolicyAction("unfollow")
  val WaitForSnapshotAction: IndexLifecyclePolicyAction    = IndexLifecyclePolicyAction("wait_for_snapshot")

}
