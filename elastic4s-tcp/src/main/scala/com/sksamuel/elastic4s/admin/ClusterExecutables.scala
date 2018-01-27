package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.cluster.{
  ClusterHealthDefinition,
  ClusterSettingsDefinition,
  ClusterStateDefinition,
  ClusterStatsDefinition
}
import com.sksamuel.elastic4s.{Executable, HealthStatus, Priority}
import org.elasticsearch.action.admin.cluster.health.{ClusterHealthRequestBuilder, ClusterHealthResponse}
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse
import org.elasticsearch.action.admin.cluster.state.{ClusterStateRequestBuilder, ClusterStateResponse}
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.health.ClusterHealthStatus

import scala.concurrent.Future

trait ClusterExecutables {

  implicit object ClusterHealthDefinitionExecutable
      extends Executable[ClusterHealthDefinition, ClusterHealthResponse, ClusterHealthResponse] {

    override def apply(c: Client, t: ClusterHealthDefinition): Future[ClusterHealthResponse] = {
      val builder = buildHealthRequest(c, t)
      injectFuture(builder.execute(_))
    }

    private[admin] def buildHealthRequest(client: Client,
                                          definition: ClusterHealthDefinition): ClusterHealthRequestBuilder = {
      val builder = client.admin.cluster().prepareHealth(definition.indices: _*)
      definition.timeout.foreach(builder.setTimeout)
      definition.waitForStatus
        .map {
          case HealthStatus.Green  => ClusterHealthStatus.GREEN
          case HealthStatus.Yellow => ClusterHealthStatus.YELLOW
          case HealthStatus.Red    => ClusterHealthStatus.RED
        }
        .foreach(builder.setWaitForStatus)
      definition.waitForNodes.foreach(builder.setWaitForNodes)
      definition.waitForActiveShards.foreach(builder.setWaitForActiveShards)
      definition.waitForNoRelocatingShards.foreach(builder.setWaitForNoRelocatingShards)
      definition.waitForEvents
        .map {
          case Priority.High      => org.elasticsearch.common.Priority.HIGH
          case Priority.Immediate => org.elasticsearch.common.Priority.IMMEDIATE
          case Priority.Languid   => org.elasticsearch.common.Priority.LANGUID
          case Priority.Low       => org.elasticsearch.common.Priority.LOW
          case Priority.Normal    => org.elasticsearch.common.Priority.NORMAL
          case Priority.Urgent    => org.elasticsearch.common.Priority.URGENT
        }
        .foreach(builder.setWaitForEvents)

      builder
    }
  }

  implicit object ClusterStatsExecutable
      extends Executable[ClusterStatsDefinition, ClusterStatsResponse, ClusterStatsResponse] {
    override def apply(c: Client, cs: ClusterStatsDefinition): Future[ClusterStatsResponse] =
      injectFuture(c.admin.cluster.prepareClusterStats.execute(_))
  }

  implicit object ClusterSettingsExecutable
      extends Executable[ClusterSettingsDefinition, ClusterUpdateSettingsResponse, ClusterUpdateSettingsResponse] {

    import scala.collection.JavaConverters._

    override def apply(c: Client, t: ClusterSettingsDefinition): Future[ClusterUpdateSettingsResponse] = {
      val builder = c.admin.cluster.prepareUpdateSettings
      builder.setPersistentSettings(t.persistentSettings.asJava)
      builder.setTransientSettings(t.transientSettings.asJava)
      injectFuture(builder.execute(_))
    }
  }

  implicit object ClusterStateExecutable
      extends Executable[ClusterStateDefinition, ClusterStateResponse, ClusterStateResponse] {
    override def apply(c: Client, t: ClusterStateDefinition): Future[ClusterStateResponse] =
      injectFuture(buildRequest(c, t).execute(_))

    private def buildRequest(c: Client, definition: ClusterStateDefinition): ClusterStateRequestBuilder = {
      val requestBuilder = c.admin().cluster().prepareState().setIndices(definition.indices: _*)

      definition.metrics.foldLeft(requestBuilder)(
        (builder, e) =>
          e match {
            case "_all"          => builder.all()
            case "blocks"        => builder.setBlocks(true)
            case "metadata"      => builder.setMetaData(true)
            case "routing_table" => builder.setRoutingTable(true)
            case "nodes"         => builder.setNodes(true)
            case _               => builder
        }
      )
    }
  }
}
