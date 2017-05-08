package com.sksamuel.elastic4s.cluster

import org.elasticsearch.cluster.health.ClusterHealthStatus
import org.elasticsearch.common.Priority
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequestBuilder

trait ClusterApi {

  def clusterState(): ClusterStateDefinition = ClusterStateDefinition()
  def clusterStats() = new ClusterStatsDefinition

  def clusterPersistentSettings(settings: Map[String, String]) = ClusterSettingsDefinition(settings, Map.empty)
  def clusterTransientSettings(settings: Map[String, String]) = ClusterSettingsDefinition(Map.empty, settings)

  def clusterHealth(): ClusterHealthDefinition = clusterHealth("_all")
  def clusterHealth(first: String, rest: String*): ClusterHealthDefinition = ClusterHealthDefinition(first +: rest)
  def clusterHealth(indices: Iterable[String]): ClusterHealthDefinition = ClusterHealthDefinition(indices.toIndexedSeq)
}

case class ClusterStatsDefinition()

case class ClusterSettingsDefinition(persistentSettings: Map[String, String],
                                     transientSettings: Map[String, String]) {

  import scala.collection.JavaConverters._

  private[elastic4s] def build(builder: ClusterUpdateSettingsRequestBuilder): ClusterUpdateSettingsRequestBuilder = {
    builder.setPersistentSettings(persistentSettings.asJava)
    builder.setTransientSettings(transientSettings.asJava)
  }

  def persistentSettings(settings: Map[String, String]): ClusterSettingsDefinition = {
    copy(persistentSettings = settings)
  }

  def transientSettings(settings: Map[String, String]): ClusterSettingsDefinition = {
    copy(transientSettings = settings)
  }
}

case class ClusterStateDefinition(metrics: Seq[String] = Seq.empty, indices: Seq[String] = Seq.empty) {

  def metrics(metrics: Seq[String]): ClusterStateDefinition = copy(metrics = metrics)
  def indices(indices: Seq[String]): ClusterStateDefinition = copy(indices = indices)

}

case class ClusterHealthDefinition(indices: Seq[String],
                                   timeout: Option[String] = None,
                                   waitForActiveShards: Option[Int] = None,
                                   waitForEvents: Option[Priority] = None,
                                   waitForStatus: Option[ClusterHealthStatus] = None,
                                   waitForNodes: Option[String] = None) {

  def timeout(value: String): ClusterHealthDefinition = copy(timeout = value.some)

  def waitForActiveShards(waitForActiveShards: Int): ClusterHealthDefinition =
    copy(waitForActiveShards = waitForActiveShards.some)

  def waitForEvents(waitForEvents: Priority): ClusterHealthDefinition = copy(waitForEvents = waitForEvents.some)

  def waitForStatus(waitForStatus: ClusterHealthStatus): ClusterHealthDefinition =
    copy(waitForStatus = waitForStatus.some)

  def waitForNodes(waitForNodes: String): ClusterHealthDefinition = copy(waitForNodes = waitForNodes.some)
}
