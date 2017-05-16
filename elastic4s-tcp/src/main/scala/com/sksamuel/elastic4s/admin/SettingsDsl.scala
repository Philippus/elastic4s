package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.{EnumConversions, Executable, Indexes}
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.admin.indices.settings.get.{GetSettingsRequest, GetSettingsResponse}
import org.elasticsearch.action.admin.indices.settings.put.{UpdateSettingsRequest, UpdateSettingsResponse}
import org.elasticsearch.client.Client

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait SettingsDsl {

  def getSettings(index: String, indexes: String*): GetSettingsDefinition = getSettings(index +: indexes)
  def getSettings(indexes: Indexes): GetSettingsDefinition = GetSettingsDefinition(indexes)

  def updateSettings(index: String, indexes: String*): UpdateSettingsDefinition = updateSettings(index +: indexes)
  def updateSettings(indexes: Indexes): UpdateSettingsDefinition = UpdateSettingsDefinition(indexes)

  implicit object GetSettingsDefinitionExecutable
    extends Executable[GetSettingsDefinition, GetSettingsResponse, GetSettingsResponse] {
    override def apply(c: Client, t: GetSettingsDefinition): Future[GetSettingsResponse] = {
      injectFuture(c.admin.indices.getSettings(t.build, _))
    }
  }

  implicit object UpdateSettingsDefinitionExecutable
    extends Executable[UpdateSettingsDefinition, UpdateSettingsResponse, UpdateSettingsResponse] {
    override def apply(c: Client, t: UpdateSettingsDefinition): Future[UpdateSettingsResponse] = {
      injectFuture(c.admin.indices.updateSettings(t.build, _))
    }
  }
}

case class GetSettingsDefinition(indexes: Indexes,
                                 options: Option[IndicesOptions] = None) {

  def build: GetSettingsRequest = {
    val req = new GetSettingsRequest().indices(indexes.values: _*)
    options.map(EnumConversions.indicesopts).foreach(req.indicesOptions)
    req
  }

  def options(options: IndicesOptions): GetSettingsDefinition = copy(options = options.some)
}

case class UpdateSettingsDefinition(indices: Indexes,
                                    preserveExisting: Option[Boolean] = None,
                                    settings: Map[String, String] = Map.empty,
                                    options: Option[IndicesOptions] = None) {

  def build: UpdateSettingsRequest = {
    val req = new UpdateSettingsRequest(indices.values: _*)
    req.settings(settings.asJava)
    preserveExisting.foreach(req.setPreserveExisting)
    options.map(EnumConversions.indicesopts).foreach(req.indicesOptions)
    req
  }

  def add(kv: (String, String)): UpdateSettingsDefinition = copy(settings = settings + kv)
  def set(kv: (String, String)): UpdateSettingsDefinition = copy(settings = Map(kv))
  def add(map: Map[String, String]): UpdateSettingsDefinition = copy(settings = settings ++ map)
  def set(map: Map[String, String]): UpdateSettingsDefinition = copy(settings = map)

  def preserveExisting(preserveExisting: Boolean): UpdateSettingsDefinition = copy(preserveExisting = preserveExisting.some)
  def options(options: IndicesOptions): UpdateSettingsDefinition = copy(options = options.some)
}
