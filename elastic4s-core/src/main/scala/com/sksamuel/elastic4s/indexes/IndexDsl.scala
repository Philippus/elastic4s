package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.{Executable, IndexAndType, Show}
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentHelper

import scala.concurrent.Future

trait IndexDsl {

  def indexInto(index: String, `type`: String): IndexDefinition = indexInto(IndexAndType(index, `type`))
  def indexInto(indexType: IndexAndType): IndexDefinition = new IndexDefinition(indexType.index, indexType.`type`)
  def index(kv: (String, String)): IndexDefinition = new IndexDefinition(kv._1, kv._2)

  implicit object IndexDefinitionExecutable
    extends Executable[IndexDefinition, IndexResponse, RichIndexResponse] {
    override def apply(c: Client, t: IndexDefinition): Future[RichIndexResponse] = {
      injectFutureAndMap(c.index(t.build, _))(RichIndexResponse.apply)
    }
  }

  implicit object IndexDefinitionShow extends Show[IndexDefinition] {
    override def show(f: IndexDefinition): String = XContentHelper.convertToJson(f.build.source, true, true)
  }

  implicit class IndexDefinitionShowOps(f: IndexDefinition) {
    def show: String = IndexDefinitionShow.show(f)
  }
}