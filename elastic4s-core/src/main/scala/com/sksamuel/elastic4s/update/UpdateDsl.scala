package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.{Executable, IndexAndTypes}
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait UpdateDsl {

  def update(id: Any) = new {
    def in(indexType: IndexAndTypes): UpdateDefinition = UpdateDefinition(indexType, id.toString)
  }

  implicit object UpdateDefinitionExecutable extends Executable[UpdateDefinition, UpdateResponse, UpdateResponse] {
    override def apply(c: Client, t: UpdateDefinition): Future[UpdateResponse] = {
      injectFuture(c.update(t.build, _))
    }
  }
}