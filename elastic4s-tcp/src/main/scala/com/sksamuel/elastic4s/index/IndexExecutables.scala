package com.sksamuel.elastic4s.index

import com.sksamuel.elastic4s.{Executable, Show}
import com.sksamuel.elastic4s.indexes.{IndexDefinition, RichIndexResponse}
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentHelper

import scala.concurrent.Future

trait IndexExecutables {

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
