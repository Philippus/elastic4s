package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.searches.{QueryDefinition, QueryDsl}
import com.sksamuel.elastic4s.{Executable, IndexAndTypes, Indexes}
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.client.Client
import org.elasticsearch.index.reindex.{BulkIndexByScrollResponse, DeleteByQueryAction}

import scala.concurrent.Future
import scala.language.implicitConversions

trait DeleteDsl extends QueryDsl {

  def deleteIn(indexes: Indexes) = new DeleteByQueryExpectsQuery(indexes)
  class DeleteByQueryExpectsQuery(indexes: Indexes) {
    def by(query: QueryDefinition): DeleteByQueryDefinition = DeleteByQueryDefinition(indexes, query)
  }

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

  implicit object DeleteByQueryDefinitionExecutable
    extends Executable[DeleteByQueryDefinition, BulkIndexByScrollResponse, BulkIndexByScrollResponse] {
    override def apply(client: Client, d: DeleteByQueryDefinition): Future[BulkIndexByScrollResponse] = {
      val builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
      d.populate(builder)
      injectFuture(builder.execute)
    }
  }
}
