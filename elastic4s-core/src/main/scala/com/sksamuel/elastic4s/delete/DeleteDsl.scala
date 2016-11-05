package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.searches.QueryDsl
import com.sksamuel.elastic4s.{Executable, IndexAndTypes}
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

trait DeleteDsl extends QueryDsl {

  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

  class DeleteByIdExpectsFrom(id: Any) {
    def from(index: String): DeleteByIdDefinition = DeleteByIdDefinition(IndexAndTypes(index), id)
    def from(indexAndTypes: IndexAndTypes): DeleteByIdDefinition = DeleteByIdDefinition(indexAndTypes, id)
  }

  implicit object DeleteByIdDefinitionExecutable
    extends Executable[DeleteByIdDefinition, DeleteResponse, DeleteResponse] {
    override def apply(c: Client, t: DeleteByIdDefinition): Future[DeleteResponse] = {
      injectFuture(c.delete(t.build, _))
    }
  }
}