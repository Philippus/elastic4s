package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.admin.cluster.health.{ClusterHealthRequest, ClusterHealthResponse}
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
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

  implicit object ClusterStatsExecutable extends Executable[ClusterStatsDefinition, ClusterStatsResponse] {
    override def apply(c: Client, cs: ClusterStatsDefinition): Future[ClusterStatsResponse] = {
      injectFuture(c.admin.cluster.prepareClusterStats.execute)
    }
  }
}

class ClusterStatsDefinition

class ClusterHealthDefinition(indices: String*) {
  val _builder = new ClusterHealthRequest(indices: _*)

  def build = _builder

  def timeout(value: String): this.type = {
    _builder.timeout(value)
    this
  }
}
