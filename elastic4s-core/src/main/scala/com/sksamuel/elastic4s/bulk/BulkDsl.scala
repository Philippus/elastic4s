package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.indexes.IndexDsl
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

trait BulkDsl {
  this: IndexDsl =>

  def bulk(requests: Iterable[BulkCompatibleDefinition]): BulkDefinition = BulkDefinition(requests.toSeq)
  def bulk(requests: BulkCompatibleDefinition*): BulkDefinition = bulk(requests)

  def bulkProcessor(): BulkProcessorBuilder = BulkProcessorBuilder()

  implicit object BulkDefinitionExecutable
    extends Executable[BulkDefinition, BulkResponse, RichBulkResponse] {
    override def apply(c: Client, t: BulkDefinition): Future[RichBulkResponse] = {
      injectFutureAndMap(c.bulk(t.build, _))(RichBulkResponse.apply)
    }
  }
}

trait BulkCompatibleDefinition