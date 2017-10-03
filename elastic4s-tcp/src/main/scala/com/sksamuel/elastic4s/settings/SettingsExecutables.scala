package com.sksamuel.elastic4s.settings

import com.sksamuel.elastic4s.{EnumConversions, Executable}
import org.elasticsearch.action.admin.indices.settings.get.{GetSettingsRequest, GetSettingsResponse}
import org.elasticsearch.action.admin.indices.settings.put.{UpdateSettingsRequest, UpdateSettingsResponse}
import org.elasticsearch.client.Client
import scala.collection.JavaConverters._

import scala.concurrent.Future

trait SettingsExecutables {

  implicit object GetSettingsDefinitionExecutable
    extends Executable[GetSettingsDefinition, GetSettingsResponse, GetSettingsResponse] {
    override def apply(c: Client, t: GetSettingsDefinition): Future[GetSettingsResponse] = {
      val req = new GetSettingsRequest().indices(t.indexes.values: _*)
      t.options.map(EnumConversions.indicesopts).foreach(req.indicesOptions)
      injectFuture(c.admin.indices.getSettings(req, _))
    }
  }

  implicit object UpdateSettingsDefinitionExecutable
    extends Executable[UpdateSettingsDefinition, UpdateSettingsResponse, UpdateSettingsResponse] {
    override def apply(c: Client, t: UpdateSettingsDefinition): Future[UpdateSettingsResponse] = {
      val req = new UpdateSettingsRequest(t.indices.values: _*)
      req.settings(t.settings.asJava)
      t.preserveExisting.foreach(req.setPreserveExisting)
      t.options.map(EnumConversions.indicesopts).foreach(req.indicesOptions)
      injectFuture(c.admin.indices.updateSettings(req, _))
    }
  }
}
