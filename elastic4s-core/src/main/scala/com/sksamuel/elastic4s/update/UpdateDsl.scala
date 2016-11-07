package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.elastic4s.{DocumentRef, Executable, IndexAndTypes, Indexes}
import org.elasticsearch.action.DocWriteResponse.Result
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.Client
import org.elasticsearch.index.reindex.{BulkIndexByScrollResponse, UpdateByQueryAction}

import scala.concurrent.Future

trait UpdateDsl {

  def update(id: Any): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: Any) {
    def in(indexType: IndexAndTypes): UpdateDefinition = UpdateDefinition(indexType, id.toString)
  }

  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes)
  class UpdateExpectsQuery(indexes: Indexes) {
    def query(query: QueryDefinition) = UpdateByQueryDefinition(indexes, query)
  }

  implicit object UpdateDefinitionExecutable
    extends Executable[UpdateDefinition, UpdateResponse, RichUpdateResponse] {
    override def apply(c: Client, t: UpdateDefinition): Future[RichUpdateResponse] = {
      injectFutureAndMap(c.update(t.build, _))(RichUpdateResponse.apply)
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

case class RichUpdateResponse(original: UpdateResponse) {
  def result = original.getResult
  def get = original.getGetResult
  def status = original.status()
  def index = original.getIndex
  def `type` = original.getType
  def id = original.getId
  def ref: DocumentRef = DocumentRef(index, `type`, id)
  def shardId = original.getShardId
  def shardInfo = original.getShardInfo
  def version = original.getVersion
  def created: Boolean = original.getResult == Result.CREATED
}