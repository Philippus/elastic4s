package com.sksamuel.elastic4s.http.cluster

import com.sksamuel.elastic4s.cluster.{ClusterHealthDefinition, ClusterStateDefinition}
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

trait ClusterImplicits {

  implicit object ClusterStateHttpExecutable extends HttpExecutable[ClusterStateDefinition, ClusterStateResponse] {
    override def execute(client: RestClient,
                         request: ClusterStateDefinition): Future[ClusterStateResponse] = {
      val endpoint = "/_cluster/state" + buildMetricsString(request.metrics) + buildIndexString(request.indices)
      logger.debug(s"Accessing endpoint $endpoint")
      client.async("GET", endpoint, Map.empty, ResponseHandler.default)
    }

    private def buildMetricsString(metrics: Seq[String]): String = {
      if (metrics.isEmpty) {
        "/_all"
      } else {
        "/" + metrics.mkString(",")
      }
    }

    private def buildIndexString(indices: Seq[String]): String = {
      if (indices.isEmpty) {
        ""
      } else {
        "/" + indices.mkString(",")
      }
    }
  }

  implicit object ClusterHealthHttpExecutable extends HttpExecutable[ClusterHealthDefinition, ClusterHealthResponse] {
    override def execute(client: RestClient,
                         request: ClusterHealthDefinition): Future[ClusterHealthResponse] = {
      val endpoint = "/_cluster/health" + indicesUrl(request.indices)

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitForStatus.map(_.toString).foreach(params.put("wait_for_status", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))
      request.waitForNodes.map(_.toString).foreach(params.put("wait_for_nodes", _))

      client.async("GET", endpoint, params.toMap, ResponseHandler.default)
    }

    private def indicesUrl(indices: Seq[String]): String = {
      if (indices.isEmpty) {
        ""
      } else {
        "/" + indices.mkString(",")
      }
    }
  }
}

object ClusterStateResponse {
  case class Index(state: String, aliases: Seq[String])
  case class Metadata(cluster_uuid: String, indices: Map[String, Index]) {
    def clusterUuid: String = cluster_uuid
  }
}

case class ClusterStateResponse(cluster_name: String, master_node: String, metadata: Option[ClusterStateResponse.Metadata]) {
  def clusterName: String = cluster_name
  def masterNode: String = master_node
}

case class ClusterHealthResponse(cluster_name: String,
                                 status: String,
                                 private val timed_out: Boolean,
                                 private val number_of_nodes: Int,
                                 private val number_of_data_nodes: Int,
                                 private val active_primary_shards: Int,
                                 private val active_shards: Int,
                                 private val relocating_shards: Int,
                                 private val initializing_shards: Int,
                                 private val unassigned_shards: Int,
                                 private val delayed_unassigned_shards: Int,
                                 private val number_of_pending_tasks: Int,
                                 private val number_of_in_flight_fetch: Int,
                                 private val task_max_waiting_in_queue_millis: Int,
                                 private val active_shards_percent_as_number: Double) {
  def clusterName: String = cluster_name
  def timeOut: Boolean = timed_out
  def numberOfNodes: Int = number_of_nodes
  def numberOfDataNodes: Int = number_of_data_nodes
  def activePrimaryShards: Int = active_primary_shards
  def activeShards: Int = active_shards
  def relocatingShards: Int = relocating_shards
  def initializingShards: Int = initializing_shards
  def unassignedShards: Int = unassigned_shards
  def delayedUnassignedShards: Int = delayed_unassigned_shards
  def numberOfPendingTasks: Int = number_of_pending_tasks
  def numberOfInFlightFetch: Int = number_of_in_flight_fetch
  def taskMaxWaitingInQueueMillis: Int = task_max_waiting_in_queue_millis
  def activeShardsPercentAsNumber: Double = active_shards_percent_as_number
}
