package com.sksamuel.elastic4s2.get

import com.sksamuel.elastic4s2.search.RichGetResponse
import com.sksamuel.elastic4s2.{Executable, IndexAndType}
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

trait GetDsl {

  def get(id: Any) = new {
    def from(index: String): GetDefinition = from(index, "_all")
    def from(index: String, `type`: String): GetDefinition = from(IndexAndType(index, `type`))
    def from(index: IndexAndType): GetDefinition = GetDefinition(index, id.toString)
  }

  implicit object GetDefinitionExecutable extends Executable[GetDefinition, GetResponse, RichGetResponse] {
    override def apply(c: Client, t: GetDefinition): Future[RichGetResponse] = {
      injectFutureAndMap(c.get(t.build, _))(RichGetResponse)
    }
  }
}
