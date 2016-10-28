package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.delete.{DeleteIndexRequest, DeleteIndexResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

/** @author Stephen Samuel */
trait DeleteIndexDsl {

  def deleteIndex(indexes: String*): DeleteIndexDefinition = deleteIndex(indexes)
  def deleteIndex(indexes: Iterable[String]): DeleteIndexDefinition = DeleteIndexDefinition(indexes.toSeq)

  implicit object DeleteIndexDefinitionExecutable
    extends Executable[DeleteIndexDefinition, DeleteIndexResponse, DeleteIndexResponse] {
    override def apply(c: Client, t: DeleteIndexDefinition): Future[DeleteIndexResponse] = {
      injectFuture(c.admin.indices.delete(t.build, _))
    }
  }
}

case class DeleteIndexDefinition(indexes: Seq[String]) {
  private val builder = new DeleteIndexRequest().indices(indexes: _*)
  def build = builder
}
