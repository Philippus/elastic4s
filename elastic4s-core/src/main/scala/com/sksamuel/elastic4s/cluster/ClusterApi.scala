package com.sksamuel.elastic4s.cluster

import org.elasticsearch.cluster.health.ClusterHealthStatus
import org.elasticsearch.common.Priority
import com.sksamuel.exts.OptionImplicits._

trait ClusterApi {

  def clusterState(): ClusterStateDefinition = ClusterStateDefinition()

  def clusterHealth(): ClusterHealthDefinition = clusterHealth("_all")
  def clusterHealth(first: String, rest: String*): ClusterHealthDefinition = ClusterHealthDefinition(first +: rest)
  def clusterHealth(indices: Iterable[String]): ClusterHealthDefinition = ClusterHealthDefinition(indices.toIndexedSeq)

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
