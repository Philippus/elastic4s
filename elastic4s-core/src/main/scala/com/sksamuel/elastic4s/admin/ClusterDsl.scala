package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.admin.cluster.health.{ClusterHealthRequest, ClusterHealthResponse}
import org.elasticsearch.action.admin.cluster.settings.{ClusterUpdateSettingsResponse, ClusterUpdateSettingsRequestBuilder}
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ClusterDsl {

  implicit object ClusterHealthDefinitionExecutable
    extends Executable[ClusterHealthDefinition, ClusterHealthResponse, ClusterHealthResponse] {
    override def apply(c: Client, t: ClusterHealthDefinition): Future[ClusterHealthResponse] = {
      injectFuture(c.admin.cluster.health(t.build, _))
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
