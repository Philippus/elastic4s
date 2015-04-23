package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.delete.{ DeleteIndexRequest, DeleteIndexResponse }
import org.elasticsearch.client.Client

import scala.concurrent.Future

/** @author Stephen Samuel */
trait DeleteIndexDsl {

  def deleteIndex(names: String*) = new DeleteIndexDefinition(names: _*)

  implicit object DeleteIndexDefinitionExecutable extends Executable[DeleteIndexDefinition, DeleteIndexResponse] {
    override def apply(c: Client, t: DeleteIndexDefinition): Future[DeleteIndexResponse] = {
      injectFuture(c.admin.indices.delete(t.build, _))
    }
  }
}

class DeleteIndexDefinition(names: String*) {
  private val builder = new DeleteIndexRequest().indices(names: _*)
  def build = builder
}
