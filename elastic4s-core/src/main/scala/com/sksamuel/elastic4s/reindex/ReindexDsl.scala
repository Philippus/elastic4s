package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.{Executable, Indexes, ProxyClients}
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.client.Client
import org.elasticsearch.index.reindex.{BulkIndexByScrollResponse, ReindexAction, ReindexRequestBuilder}

import scala.concurrent.Future

trait ReindexDsl {

  def reindex(sourceIndexes: Indexes): ReindexExpectsTarget = new ReindexExpectsTarget(sourceIndexes)
  class ReindexExpectsTarget(sourceIndexes: Indexes) {
    def into(index: String): ReindexDefinition = ReindexDefinition(sourceIndexes, index)
    def into(index: String, `type`: String): ReindexDefinition = ReindexDefinition(sourceIndexes, index, `type`.some)
  }

  implicit object SearchDefinitionExecutable
    extends Executable[ReindexDefinition, BulkIndexByScrollResponse, BulkIndexByScrollResponse] {
    override def apply(c: Client, r: ReindexDefinition): Future[BulkIndexByScrollResponse] = {
      val builder = new ReindexRequestBuilder(ProxyClients.client, ReindexAction.INSTANCE)
      r.populate(builder)
      injectFuture(builder.execute)
    }
  }
}
