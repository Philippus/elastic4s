package com.sksamuel.elastic4s.index

import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.mappings.XContentFieldValueWriter
import com.sksamuel.elastic4s.{Executable, Show}
import org.elasticsearch.action.index.IndexRequest.OpType
import org.elasticsearch.action.index.{IndexRequestBuilder, IndexResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentFactory

import scala.concurrent.Future

trait IndexExecutables {

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
      t.routing.foreach(builder.setRouting)
      t.pipeline.foreach(builder.setPipeline)
      t.timestamp.foreach(builder.setTimestamp)
      t.timestamp.foreach(builder.setSource)
      t.opType.foreach(builder.setOpType)
      builder
    }

    override def apply(c: Client,
                       t: IndexDefinition): Future[RichIndexResponse] = {
      val req = builder(c, t)
      injectFutureAndMap(req.execute)(RichIndexResponse.apply)
    }
  }

  implicit object IndexDefinitionShow
    extends Show[IndexDefinition] {
    override def show(f: IndexDefinition): String = f.toString
  }

  implicit class IndexDefinitionShowOps(f: IndexDefinition) {
    def show: String = IndexDefinitionShow.show(f)
  }
}
