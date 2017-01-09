package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.get.{GetResponse, MultiGetResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.index.VersionType

import scala.concurrent.Future

trait GetExecutables {

  implicit object GetDefinitionExecutable extends Executable[GetDefinition, GetResponse, RichGetResponse] {
    override def apply(c: Client, t: GetDefinition): Future[RichGetResponse] = {

      val req = c.prepareGet(t.indexAndType.index, t.indexAndType.`type`, t.id)

      if (t.storedFields.nonEmpty)
        req.setStoredFields(t.storedFields: _*)
      t.realtime.foreach(req.setRealtime)
      t.routing.foreach(req.setRouting)
      t.refresh.foreach(req.setRefresh)
      t.parent.foreach(req.setParent)
      t.fetchSource.foreach { context =>
        req.setFetchSource(context.enabled).setFetchSource(context.includes.toArray, context.excludes.toArray)
      }
      t.preference.foreach(req.setPreference)
      t.version.foreach(req.setVersion)
      t.versionType.map(VersionType.fromString).foreach(req.setVersionType)

      injectFutureAndMap(req.execute)(RichGetResponse)
    }
  }

  implicit object MultiGetDefinitionExecutable
    extends Executable[MultiGetDefinition, MultiGetResponse, RichMultiGetResponse] {
    override def apply(c: Client, t: MultiGetDefinition): Future[RichMultiGetResponse] = {
      val builder = c.prepareMultiGet()
      t.populate(builder)
      injectFutureAndMap(builder.execute)(RichMultiGetResponse.apply)
    }
  }
}
