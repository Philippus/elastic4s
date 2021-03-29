package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.cluster.{AddRemoteClusterSettingsRequest, ClusterHealthRequest, ClusterSettingsRequest, ClusterStateRequest, ClusterStatsRequest, NodeHotThreadsRequest, NodeUsageRequest, RemoteClusterInfoRequest}

trait ClusterApi {

  def clusterState(): ClusterStateRequest = ClusterStateRequest()
  def clusterStats() = new ClusterStatsRequest

  def nodeUsage(): NodeUsageRequest = NodeUsageRequest()
  def nodeUsage(nodeId: String): NodeUsageRequest = NodeUsageRequest(nodeId = Some(nodeId))

  def nodeHotThreads(): NodeHotThreadsRequest = NodeHotThreadsRequest()
  def nodeHotThreads(nodeId: String): NodeHotThreadsRequest = NodeHotThreadsRequest(nodeId = Some(nodeId))

  def clusterPersistentSettings(settings: Map[String, String]): ClusterSettingsRequest =
    ClusterSettingsRequest(settings, Map.empty)

  def clusterTransientSettings(settings: Map[String, String]): ClusterSettingsRequest =
    ClusterSettingsRequest(Map.empty, settings)

  def clusterHealth(): ClusterHealthRequest = clusterHealth("_all")
  def clusterHealth(first: String, rest: String*): ClusterHealthRequest = ClusterHealthRequest(first +: rest)
  def clusterHealth(indices: Iterable[String]): ClusterHealthRequest = ClusterHealthRequest(indices.toIndexedSeq)
  def remoteClusterInfo() = RemoteClusterInfoRequest()
  def addRemoteClusterRequest(settings: Map[String, String]) = AddRemoteClusterSettingsRequest(clusterPersistentSettings(settings))
}
