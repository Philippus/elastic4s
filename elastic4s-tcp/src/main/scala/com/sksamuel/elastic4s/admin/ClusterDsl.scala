package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.cluster.ClusterStateDefinition
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse
import org.elasticsearch.action.admin.cluster.settings.{ClusterUpdateSettingsRequestBuilder, ClusterUpdateSettingsResponse}
import org.elasticsearch.action.admin.cluster.state.{ClusterStateRequestBuilder, ClusterStateResponse}
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ClusterDsl {

  def clusterPersistentSettings(settings: Map[String, String]) = ClusterSettingsDefinition(settings, Map.empty)
  def clusterTransientSettings(settings: Map[String, String]) = ClusterSettingsDefinition(Map.empty, settings)

  def clusterStats() = new ClusterStatsDefinition

  def clusterHealth(): ClusterHealthDefinition = clusterHealth("_all")
  def clusterHealth(first: String, rest: String*): ClusterHealthDefinition = ClusterHealthDefinition(first +: rest)
  def clusterHealth(indices: Iterable[String]): ClusterHealthDefinition = ClusterHealthDefinition(indices.toIndexedSeq)

  implicit object ClusterHealthDefinitionExecutable
    extends Executable[ClusterHealthDefinition, ClusterHealthResponse, ClusterHealthResponse] {

    override def apply(c: Client, t: ClusterHealthDefinition): Future[ClusterHealthResponse] = {
      val builder = c.admin.cluster().prepareHealth(t.indices: _*)
      t.build(builder)
      injectFuture(builder.execute())
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

class ClusterStatsDefinition

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
