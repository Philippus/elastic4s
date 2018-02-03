package com.sksamuel.elastic4s.http.cluster

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.cluster.{ClusterHealthRequest, ClusterStateRequest}
import com.sksamuel.elastic4s.http.{ElasticRequest, Handler}

trait ClusterHandlers {

  implicit object ClusterStateHandler extends Handler[ClusterStateRequest, ClusterStateResponse] {

    override def requestHandler(request: ClusterStateRequest): ElasticRequest = {
      val endpoint = "/_cluster/state" + buildMetricsString(request.metrics) + buildIndexString(request.indices)
      ElasticRequest("GET", endpoint)
    }

    private def buildMetricsString(metrics: Seq[String]): String =
      if (metrics.isEmpty) {
        "/_all"
      } else {
        "/" + metrics.mkString(",")
      }

    private def buildIndexString(indices: Seq[String]): String =
      if (indices.isEmpty) {
        ""
      } else {
        "/" + indices.mkString(",")
      }
  }

  implicit object ClusterHealthHandler extends Handler[ClusterHealthRequest, ClusterHealthResponse] {

    override def requestHandler(request: ClusterHealthRequest): ElasticRequest = {
      val endpoint = "/_cluster/health" + indicesUrl(request.indices)

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitForStatus.map(_.toString).foreach(params.put("wait_for_status", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))
      request.waitForNodes.map(_.toString).foreach(params.put("wait_for_nodes", _))
      request.waitForNoRelocatingShards.map(_.toString).foreach(params.put("wait_for_no_relocating_shards", _))
      request.timeout.map(_.toString).foreach(params.put("timeout", _))

      ElasticRequest("GET", endpoint, params.toMap)
    }

    private def indicesUrl(indices: Seq[String]): String =
      if (indices.isEmpty) {
        ""
      } else {
        "/" + indices.mkString(",")
      }
  }
}

object ClusterStateResponse {

  case class Index(state: String, aliases: Seq[String])

  case class Metadata(@JsonProperty("cluster_uuid") clusterUuid: String, indices: Map[String, Index])
}

case class ClusterStateResponse(@JsonProperty("cluster_name") clusterName: String,
                                @JsonProperty("master_node") masterNode: String,
                                @JsonProperty("compressed_size_in_bytes") compressedSizeInBytes: Long,
                                @JsonProperty("state_uuid") stateUuid: String,
                                metadata: Option[ClusterStateResponse.Metadata])

case class ClusterHealthResponse(@JsonProperty("cluster_name") clusterName: String,
                                 status: String,
                                 @JsonProperty("timed_out") timeOut: Boolean,
                                 @JsonProperty("number_of_nodes") numberOfNodes: Int,
                                 @JsonProperty("number_of_data_nodes") numberOfDataNodes: Int,
                                 @JsonProperty("active_primary_shards") activePrimaryShards: Int,
                                 @JsonProperty("active_shards") activeShards: Int,
                                 @JsonProperty("relocating_shards") relocatingShards: Int,
                                 @JsonProperty("initializing_shards") initializingShards: Int,
                                 @JsonProperty("unassigned_shards") unassignedShards: Int,
                                 @JsonProperty("delayed_unassigned_shards") delayedUnassignedShards: Int,
                                 @JsonProperty("number_of_pending_tasks") numberOfPendingTasks: Int,
                                 @JsonProperty("number_of_in_flight_fetch") numberOfInFlightFetch: Int,
                                 @JsonProperty("task_max_waiting_in_queue_millis") taskMaxWaitingInQueueMillis: Int,
                                 @JsonProperty("active_shards_percent_as_number") activeShardsPercentAsNumber: Double)
