package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.settings.get.{GetSettingsRequest, GetSettingsResponse}
import org.elasticsearch.action.admin.indices.settings.put.{UpdateSettingsResponse, UpdateSettingsRequest}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait SettingsDsl {

  implicit object GetSettingsDefinitionExecutable extends Executable[GetSettingsDefinition, GetSettingsResponse] {
    override def apply(c: Client, t: GetSettingsDefinition): Future[GetSettingsResponse] = {
      injectFuture(c.admin.indices.getSettings(t.build, _))
    }
  }

  implicit object UpdateSettingsDefinitionExecutable
    extends Executable[UpdateSettingsDefinition, UpdateSettingsResponse] {
    override def apply(c: Client, t: UpdateSettingsDefinition): Future[UpdateSettingsResponse] = {
      injectFuture(c.admin.indices.updateSettings(t.builder, _))
    }
  }
}

class GetSettingsDefinition(indexes: Seq[String]) {
  def build: GetSettingsRequest = new GetSettingsRequest().indices(indexes: _*)
}

class UpdateSettingsDefinition(index: String) {

  val builder = new UpdateSettingsRequest(index)

  def set(map: Map[String, String]): this.type = {
    import scala.collection.JavaConverters._
    builder.settings(map.asJava)
    this
  }
}