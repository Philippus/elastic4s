package com.sksamuel.elastic4s.cluster

trait ClusterApi {

  def clusterState(): ClusterStateDefinition = ClusterStateDefinition()

}

case class ClusterStateDefinition(metrics: Seq[String] = Seq.empty, indices: Seq[String] = Seq.empty) {

  def metrics(metrics: Seq[String]): ClusterStateDefinition = copy(metrics = metrics)
  def indices(indices: Seq[String]): ClusterStateDefinition = copy(indices = indices)

}
