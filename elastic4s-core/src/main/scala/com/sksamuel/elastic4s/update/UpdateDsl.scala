package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.elastic4s.{Executable, IndexAndTypes, Indexes}
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.Client
import org.elasticsearch.index.reindex.{BulkIndexByScrollResponse, UpdateByQueryAction}

import scala.concurrent.Future

trait UpdateDsl {

  def update(id: Any): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: Any) {
    def in(indexType: IndexAndTypes): UpdateDefinition = UpdateDefinition(indexType, id.toString)
  }

  def update(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes)
  class UpdateExpectsQuery(indexes: Indexes) {
    def query(query: QueryDefinition) = UpdateByQueryDefinition(indexes, query)
  }

  implicit object UpdateDefinitionExecutable
    extends Executable[UpdateDefinition, UpdateResponse, UpdateResponse] {
    override def apply(c: Client, t: UpdateDefinition): Future[UpdateResponse] = {
      injectFuture(c.update(t.build, _))
    }
  }

  implicit object UpdateByQueryDefinitionExecutable
    extends Executable[UpdateByQueryDefinition, BulkIndexByScrollResponse, BulkIndexByScrollResponse] {
    override def apply(c: Client, t: UpdateByQueryDefinition): Future[BulkIndexByScrollResponse] = {
      val builder = UpdateByQueryAction.INSTANCE.newRequestBuilder(c)
      t.populate(builder)
      injectFuture(builder.execute)
    }
  }
}
