package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.cluster.health.{ ClusterHealthRequest, ClusterHealthResponse }
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ClusterDsl {
  def clusterHealth = new ClusterHealthDefinition()
  def clusterHealth(indices: String*) = new ClusterHealthDefinition(indices: _*)

  implicit object ClusterHealthDefinitionExecutable
      extends Executable[ClusterHealthDefinition, ClusterHealthResponse] {
    override def apply(c: Client, t: ClusterHealthDefinition): Future[ClusterHealthResponse] = {
      injectFuture(c.admin.cluster.health(t.build, _))
    }
  }
}

class ClusterHealthDefinition(indices: String*) {
  def build = new ClusterHealthRequest(indices: _*)
}
