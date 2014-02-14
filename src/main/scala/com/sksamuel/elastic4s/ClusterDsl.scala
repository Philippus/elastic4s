package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.cluster.health.{ClusterHealthRequest, ClusterHealthAction}

trait ClusterDsl {
  def clusterHealth = new ClusterHealth()

  def clusterHealth(indices: String*) = new ClusterHealth(indices: _*)

  class ClusterHealth(indices: String*) extends ClusterRequestDefinition(ClusterHealthAction.INSTANCE) {
    def build = new ClusterHealthRequest(indices: _*)
  }
}
