package com.sksamuel.elastic4s.requests.cluster

import com.sksamuel.elastic4s.requests.common.{HealthStatus, Priority}
import com.sksamuel.exts.OptionImplicits._

trait ClusterApi {

  def clusterState(): ClusterStateRequest = ClusterStateRequest()
  def clusterStats()                      = new ClusterStatsRequest

  def clusterPersistentSettings(settings: Map[String, String]) = ClusterSettingsRequest(settings, Map.empty)
  def clusterTransientSettings(settings: Map[String, String])  = ClusterSettingsRequest(Map.empty, settings)

  def clusterHealth(): ClusterHealthRequest                             = clusterHealth("_all")
  def clusterHealth(first: String, rest: String*): ClusterHealthRequest = ClusterHealthRequest(first +: rest)
  def clusterHealth(indices: Iterable[String]): ClusterHealthRequest    = ClusterHealthRequest(indices.toIndexedSeq)
}

case class ClusterStatsRequest()

case class ClusterSettingsRequest(persistentSettings: Map[String, String], transientSettings: Map[String, String]) {

  def persistentSettings(settings: Map[String, String]): ClusterSettingsRequest =
    copy(persistentSettings = settings)

  def transientSettings(settings: Map[String, String]): ClusterSettingsRequest =
    copy(transientSettings = settings)
}

case class ClusterStateRequest(metrics: Seq[String] = Seq.empty, indices: Seq[String] = Seq.empty) {

  def metrics(metrics: Seq[String]): ClusterStateRequest = copy(metrics = metrics)
  def indices(indices: Seq[String]): ClusterStateRequest = copy(indices = indices)

}

case class ClusterHealthRequest(indices: Seq[String],
                                timeout: Option[String] = None,
                                waitForActiveShards: Option[Int] = None,
                                waitForEvents: Option[Priority] = None,
                                waitForStatus: Option[HealthStatus] = None,
                                waitForNodes: Option[String] = None,
                                waitForNoRelocatingShards: Option[Boolean] = None) {

  def timeout(value: String): ClusterHealthRequest = copy(timeout = value.some)

  def waitForActiveShards(waitForActiveShards: Int): ClusterHealthRequest =
    copy(waitForActiveShards = waitForActiveShards.some)

  def waitForEvents(waitForEvents: Priority): ClusterHealthRequest = copy(waitForEvents = waitForEvents.some)

  def waitForStatus(waitForStatus: HealthStatus): ClusterHealthRequest =
    copy(waitForStatus = waitForStatus.some)

  def waitForNodes(waitForNodes: String): ClusterHealthRequest = copy(waitForNodes = waitForNodes.some)

  def waitForNoRelocatingShards(waitForNoRelocatingShards: Boolean): ClusterHealthRequest =
    copy(waitForNoRelocatingShards = waitForNoRelocatingShards.some)
}
