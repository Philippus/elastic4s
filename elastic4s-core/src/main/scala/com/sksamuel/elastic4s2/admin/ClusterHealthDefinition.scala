package com.sksamuel.elastic4s2.admin

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder
import org.elasticsearch.common.Priority

case class ClusterHealthDefinition(indices: Seq[String],
                                   timeout: Option[String] = None,
                                   waitForActiveShards: Option[Int] = None,
                                   waitForEvents: Option[Priority] = None,
                                   waitForNodes: Option[String] = None) {

  def build(builder: ClusterHealthRequestBuilder): Unit = {
    timeout.foreach(builder.setTimeout)
    waitForNodes.foreach(builder.setWaitForNodes)
    waitForActiveShards.foreach(builder.setWaitForActiveShards)
    waitForEvents.foreach(builder.setWaitForEvents)
  }

  def timeout(value: String): ClusterHealthDefinition = copy(timeout = value.some)

  def waitForActiveShards(waitForActiveShards: Int): ClusterHealthDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)

  def waitForEvents(waitForEvents: Priority): ClusterHealthDefinition = copy(waitForEvents = waitForEvents.some)

  def waitForNodes(waitForNodes: String): ClusterHealthDefinition = copy(waitForNodes = waitForNodes.some)
}
