package com.sksamuel.elastic4s2.index

import com.sksamuel.elastic4s2.Executable
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

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
