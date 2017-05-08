package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.cluster.{ClusterHealthDefinition, ClusterSettingsDefinition, ClusterStateDefinition, ClusterStatsDefinition}
import org.elasticsearch.action.admin.cluster.health.{ClusterHealthRequestBuilder, ClusterHealthResponse}
import org.elasticsearch.action.admin.cluster.settings.{ClusterUpdateSettingsRequestBuilder, ClusterUpdateSettingsResponse}
import org.elasticsearch.action.admin.cluster.state.{ClusterStateRequestBuilder, ClusterStateResponse}
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ClusterExecutables {

  implicit object ClusterHealthDefinitionExecutable
    extends Executable[ClusterHealthDefinition, ClusterHealthResponse, ClusterHealthResponse] {

    override def apply(c: Client, t: ClusterHealthDefinition): Future[ClusterHealthResponse] = {
      val builder = buildHealthRequest(c, t)
      injectFuture(builder.execute())
    }

    private[admin] def buildHealthRequest(client: Client, definition: ClusterHealthDefinition): ClusterHealthRequestBuilder = {
      val builder = client.admin.cluster().prepareHealth(definition.indices: _*)
      definition.timeout.foreach(builder.setTimeout)
      definition.waitForStatus.foreach(builder.setWaitForStatus)
      definition.waitForNodes.foreach(builder.setWaitForNodes)
      definition.waitForActiveShards.foreach(builder.setWaitForActiveShards)
      definition.waitForEvents.foreach(builder.setWaitForEvents)

      builder
    }
  }

  implicit object ClusterStatsExecutable
    extends Executable[ClusterStatsDefinition, ClusterStatsResponse, ClusterStatsResponse] {
    override def apply(c: Client, cs: ClusterStatsDefinition): Future[ClusterStatsResponse] = {
      injectFuture(c.admin.cluster.prepareClusterStats.execute)
    }
  }

  implicit object ClusterSettingsExecutable
    extends Executable[ClusterSettingsDefinition, ClusterUpdateSettingsResponse, ClusterUpdateSettingsResponse] {
    override def apply(c: Client, t: ClusterSettingsDefinition): Future[ClusterUpdateSettingsResponse] = {
      injectFuture(t.build(c.admin.cluster.prepareUpdateSettings).execute)
    }
  }

  implicit object ClusterStateExecutable
    extends Executable[ClusterStateDefinition, ClusterStateResponse, ClusterStateResponse] {
    override def apply(c: Client, t: ClusterStateDefinition): Future[ClusterStateResponse] = {
      injectFuture(buildRequest(c, t).execute)
    }

    private def buildRequest(c: Client, definition: ClusterStateDefinition): ClusterStateRequestBuilder = {
      val requestBuilder = c.admin().cluster().prepareState().setIndices(definition.indices:_*)

      definition.metrics.foldLeft(requestBuilder)((builder, e) =>
        e match {
          case "_all" => builder.all()
          case "blocks" => builder.setBlocks(true)
          case "metadata" => builder.setMetaData(true)
          case "routing_table" => builder.setRoutingTable(true)
          case "nodes" => builder.setNodes(true)
          case _ => builder
        }
      )
    }
  }
}
