package com.sksamuel.elastic4s.http.cluster

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.cluster.{ClusterHealthRequest, ClusterSettingsRequest, ClusterStateRequest}
import com.sksamuel.elastic4s.http.{ElasticRequest, EnumConversions, Handler, HttpEntity}
import org.apache.http.entity.ContentType

trait ClusterHandlers {

  implicit object ClusterStateHandler extends Handler[ClusterStateRequest, ClusterStateResponse] {

    override def build(request: ClusterStateRequest): ElasticRequest = {
      val endpoint = "/_cluster/state" + buildMetricsString(request.metrics) + buildIndexString(request.indices)
      ElasticRequest("GET", endpoint)
    }

    private def buildMetricsString(metrics: Seq[String]): String =
      if (metrics.isEmpty)
        "/_all"
      else
        "/" + metrics.mkString(",")

    private def buildIndexString(indices: Seq[String]): String =
      if (indices.isEmpty)
        ""
      else
        "/" + indices.mkString(",")
  }

  implicit object ClusterHealthHandler extends Handler[ClusterHealthRequest, ClusterHealthResponse] {

    override def build(request: ClusterHealthRequest): ElasticRequest = {
      val endpoint = "/_cluster/health" + indicesUrl(request.indices)

      val params = scala.collection.mutable.Map.empty[String, String]
      request.waitForStatus.map(_.toString).foreach(params.put("wait_for_status", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))
      request.waitForNodes.map(_.toString).foreach(params.put("wait_for_nodes", _))
      request.waitForNoRelocatingShards.map(_.toString).foreach(params.put("wait_for_no_relocating_shards", _))
      request.timeout.map(_.toString).foreach(params.put("timeout", _))
      request.level.map(EnumConversions.clusterHealthLevel).foreach(params.put("level", _))

      ElasticRequest("GET", endpoint, params.toMap)
    }

    private def indicesUrl(indices: Seq[String]): String =
      if (indices.isEmpty)
        ""
      else
        "/" + indices.mkString(",")
  }

  implicit object ClusterSettingsHandler extends Handler[ClusterSettingsRequest, ClusterSettingsResponse] {
    override def build(request: ClusterSettingsRequest): ElasticRequest = {
      val builder = ClusterBodyBuilderFn(request)
      val entity = HttpEntity(builder.string(), ContentType.APPLICATION_JSON.getMimeType)
      ElasticRequest("PUT", "/_cluster/settings", Map("flat_settings" -> true), entity)
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
                                 @JsonProperty("active_shards_percent_as_number") activeShardsPercentAsNumber: Double,
                                 indices: Map[String, ClusterHealthIndex])

case class ClusterHealthIndex(status: String,
                              @JsonProperty("number_of_shards") numberOfShards: Int,
                              @JsonProperty("number_of_replicas") numberOfReplicas: Int,
                              @JsonProperty("active_primary_shards") activePrimaryShards: Int,
                              @JsonProperty("active_shards") activeShards: Int,
                              @JsonProperty("relocating_shards") relocatingShards: Int,
                              @JsonProperty("initializing_shards") initializingShards: Int,
                              @JsonProperty("unassigned_shards") unassignedShards: Int,
                              shards: Map[String, ClusterHealthShard])

case class ClusterHealthShard(status: String,
                              @JsonProperty("primary_active") primaryActive: Boolean,
                              @JsonProperty("active_shards") activeShards: Int,
                              @JsonProperty("relocating_shards") relocatingShards: Int,
                              @JsonProperty("initializing_shards") initializingShards: Int,
                              @JsonProperty("unassigned_shards") unassignedShards: Int)

case class ClusterSettingsResponse(persistent: Map[String, String], transient: Map[String, String])
