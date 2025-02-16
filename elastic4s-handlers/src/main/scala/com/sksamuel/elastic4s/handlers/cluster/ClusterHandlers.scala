package com.sksamuel.elastic4s.handlers.cluster

import com.sksamuel.elastic4s.requests.cluster.{
  AddRemoteClusterResponse,
  AddRemoteClusterSettingsRequest,
  ClusterHealthRequest,
  ClusterHealthResponse,
  ClusterSettingsRequest,
  ClusterSettingsResponse,
  ClusterStateRequest,
  ClusterStateResponse,
  ClusterStatsRequest,
  ClusterStatsResponse,
  GetClusterSettingsRequest,
  RemoteClusterInfo,
  RemoteClusterInfoRequest
}
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity}

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

  implicit object GetClusterSettingsHandler extends Handler[GetClusterSettingsRequest, ClusterSettingsResponse] {
    override def build(request: GetClusterSettingsRequest): ElasticRequest = {
      ElasticRequest("GET", "/_cluster/settings", Map("flat_settings" -> true))
    }
  }

  implicit object ClusterSettingsHandler extends Handler[ClusterSettingsRequest, ClusterSettingsResponse] {
    override def build(request: ClusterSettingsRequest): ElasticRequest = {
      val builder = ClusterSettingsBodyBuilderFn(request)
      val entity  = HttpEntity(builder.string, "application/json")
      ElasticRequest("PUT", "/_cluster/settings", Map("flat_settings" -> true), entity)
    }
  }

  implicit val nodeUsageHandler: NodeUsageHandler.type           = NodeUsageHandler
  implicit val nodeHotThreadsHandler: NodeHotThreadsHandler.type = NodeHotThreadsHandler

  implicit object ClusterHealthHandler extends Handler[ClusterHealthRequest, ClusterHealthResponse] {

    override def build(request: ClusterHealthRequest): ElasticRequest = {
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
      if (indices.isEmpty)
        ""
      else
        "/" + indices.mkString(",")
  }

  implicit object ClusterStatsHandler extends Handler[ClusterStatsRequest, ClusterStatsResponse] {

    private val Method   = "GET"
    private val Endpoint = "/_cluster/stats?human&pretty"

    override def build(t: ClusterStatsRequest): ElasticRequest = {
      ElasticRequest(Method, Endpoint)
    }
  }

  type RemoteClusterInfoResponse = Map[String, RemoteClusterInfo]

  implicit object RemoteClusterInfoHandler extends Handler[RemoteClusterInfoRequest, RemoteClusterInfoResponse] {
    override def build(request: RemoteClusterInfoRequest): ElasticRequest = {
      ElasticRequest("GET", "/_remote/info")
    }
  }

  implicit object AddRemoteClusterSettingsHandler
      extends Handler[AddRemoteClusterSettingsRequest, AddRemoteClusterResponse] {
    override def build(request: AddRemoteClusterSettingsRequest): ElasticRequest = {
      ClusterSettingsHandler.build(request.settingsRequest)
    }
  }
}
