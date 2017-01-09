package com.sksamuel.elastic4s.reindex

import com.sksamuel.elastic4s.{Executable, ProxyClients}
import org.elasticsearch.client.Client
import org.elasticsearch.index.reindex.{BulkIndexByScrollResponse, ReindexAction, ReindexRequestBuilder}

import scala.concurrent.Future

trait ReindexExecutables {
  implicit object ReindexDefinitionExecutable
    extends Executable[ReindexDefinition, BulkIndexByScrollResponse, BulkIndexByScrollResponse] {
    override def apply(c: Client, r: ReindexDefinition): Future[BulkIndexByScrollResponse] = {
      val builder = new ReindexRequestBuilder(ProxyClients.client, ReindexAction.INSTANCE)
      r.populate(builder)
      injectFuture(builder.execute)
    }
  }
}
