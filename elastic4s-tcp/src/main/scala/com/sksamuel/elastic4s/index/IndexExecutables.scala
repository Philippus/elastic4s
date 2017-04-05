package com.sksamuel.elastic4s.index

import com.sksamuel.elastic4s.indexes.{IndexDefinition, IndexShowImplicits}
import com.sksamuel.elastic4s.{Executable, XContentFieldValueWriter}
import org.elasticsearch.action.index.{IndexRequestBuilder, IndexResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentFactory

import scala.concurrent.Future

trait IndexExecutables extends IndexShowImplicits {

  implicit object IndexDefinitionExecutable
    extends Executable[IndexDefinition, IndexResponse, RichIndexResponse] {

    def builder(c: Client, t: IndexDefinition): IndexRequestBuilder = {
      val builder = c.prepareIndex(t.indexAndType.index, t.indexAndType.`type`)
      t.id.map(_.toString).foreach(builder.setId)
      t.source match {
        case Some(json) => builder.setSource(json)
        case _ =>
          val source = XContentFactory.jsonBuilder().startObject()
          t.fields.foreach(XContentFieldValueWriter(source, _))
          source.endObject()
          builder.setSource(source)
      }
      t.parent.foreach(builder.setParent)
      t.refresh.foreach(builder.setRefreshPolicy)
      t.version.foreach(builder.setVersion)
      t.versionType.foreach(builder.setVersionType)
      t.routing.foreach(builder.setRouting)
      t.pipeline.foreach(builder.setPipeline)
      t.timestamp.foreach(builder.setTimestamp)
      t.source.foreach(builder.setSource)
      t.createOnly.foreach(builder.setCreate)
      builder
    }

    override def apply(c: Client,
                       t: IndexDefinition): Future[RichIndexResponse] = {
      val req = builder(c, t)
      injectFutureAndMap(req.execute)(RichIndexResponse.apply)
    }
  }

  implicit class IndexDefinitionShowOps(f: IndexDefinition) {
    def show: String = IndexShow.show(f)
  }
}
