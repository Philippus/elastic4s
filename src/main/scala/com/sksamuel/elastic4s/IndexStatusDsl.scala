package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.status.{ IndicesStatusRequest, IndicesStatusResponse }
import org.elasticsearch.client.Client

import scala.concurrent.Future

@deprecated("Use the recovery API", "1.3.0")
trait IndexStatusDsl {
  def status(indices: String*) = new IndexStatusDefinition(indices: _*)

  @deprecated("Use the recovery API", "1.3.0")
  class IndexStatusDefinition(indices: String*) {
    private def builder = new IndicesStatusRequest(indices: _*)
    def build = builder
  }

  implicit object IndexStatusDefinitionExecutable
      extends Executable[IndexStatusDefinition, IndicesStatusResponse] {
    override def apply(c: Client, t: IndexStatusDefinition): Future[IndicesStatusResponse] = {
      injectFuture(c.admin.indices.status(t.build, _))
    }
  }
}
