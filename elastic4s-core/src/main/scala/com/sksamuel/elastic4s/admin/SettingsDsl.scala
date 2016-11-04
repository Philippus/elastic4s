package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.{Executable, Indexes}
import org.elasticsearch.action.admin.indices.settings.get.{GetSettingsRequest, GetSettingsResponse}
import org.elasticsearch.action.admin.indices.settings.put.{UpdateSettingsRequest, UpdateSettingsResponse}
import org.elasticsearch.action.support.IndicesOptions
import org.elasticsearch.client.Client
import com.sksamuel.exts.OptionImplicits._
import scala.concurrent.Future

trait SettingsDsl {

  def updateSettings(index: String) = new UpdateSettingsDefinition(index)

  implicit object GetSettingsDefinitionExecutable
    extends Executable[GetSettingsDefinition, GetSettingsResponse, GetSettingsResponse] {
    override def apply(c: Client, t: GetSettingsDefinition): Future[GetSettingsResponse] = {
      injectFuture(c.admin.indices.getSettings(t.build, _))
    }
  }

  implicit object UpdateSettingsDefinitionExecutable
    extends Executable[UpdateSettingsDefinition, UpdateSettingsResponse, UpdateSettingsResponse] {
    override def apply(c: Client, t: UpdateSettingsDefinition): Future[UpdateSettingsResponse] = {
      injectFuture(c.admin.indices.updateSettings(t.builder, _))
    }
  }
}

case class GetSettingsDefinition(indexes: Indexes,
                                 options: Option[IndicesOptions] = None) {
  def build: GetSettingsRequest = {
    val req = new GetSettingsRequest().indices(indexes.values: _*)
    options.foreach(req.indicesOptions)
    req
  }
  def options(options: IndicesOptions): GetSettingsDefinition = copy(options = options.some)
}

class UpdateSettingsDefinition(index: String) {

  val builder = new UpdateSettingsRequest(index)

  def set(map: Map[String, String]): this.type = {
    import scala.collection.JavaConverters._
    builder.settings(map.asJava)
    this
  }
}
