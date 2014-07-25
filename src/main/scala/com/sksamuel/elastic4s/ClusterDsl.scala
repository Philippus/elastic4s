package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest

trait ClusterDsl {
  def clusterHealth = new ClusterHealthDefinition()
  def clusterHealth(indices: String*) = new ClusterHealthDefinition(indices: _*)
}

class ClusterHealthDefinition(indices: String*) {
  def build = new ClusterHealthRequest(indices: _*)
}
