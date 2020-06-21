package com.sksamuel.elastic4s.requests.cluster

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.{ElasticRequest, Handler}
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.Duration

object NodeUsageHandler extends Handler[NodeUsageRequest, NodeUsageResponse] {
  override def build(t: NodeUsageRequest): ElasticRequest = {

    val endpoint = t.nodeId match {
      case Some(nodeId) => s"/_nodes/$nodeId/usage"
      case _ => "/_nodes/usage"
    }

    val params = scala.collection.mutable.Map.empty[String, String]
    t.masterTimeout.foreach(params.put("master_timeout", _))
    t.timeout.foreach(params.put("timeout", _))

    ElasticRequest("GET", endpoint, params.toMap)
  }
}

case class NodeUsageRequest(nodeId: Option[String] = None,
                            // Specifies the period of time to wait for a connection to the master node
                            masterTimeout: Option[String] = None,
                            // Specifies the period of time to wait for a response
                            timeout: Option[String] = None) {

  def nodeId(nodeId: String): NodeUsageRequest = copy(nodeId = nodeId.some)

  def masterTimeout(timeout: Duration): NodeUsageRequest = copy(masterTimeout = (timeout.toNanos + "n").some)
  def masterTimeout(timeout: String): NodeUsageRequest = copy(masterTimeout = timeout.some)

  def timeout(timeout: Duration): NodeUsageRequest = copy(timeout = (timeout.toNanos + "n").some)
  def timeout(timeout: String): NodeUsageRequest = copy(timeout = timeout.some)
}

case class NodeUsage(timestamp: Long, since: Long, @JsonProperty("rest_actions") restActions: Map[String, Int])
case class NodeCounts(total: Int, successful: Int, failed: Int)
case class NodeUsageResponse(@JsonProperty("_nodes") nodeCounts: NodeCounts,
                             @JsonProperty("cluster_name") clusterName: String,
                             nodes: Map[String, NodeUsage])
