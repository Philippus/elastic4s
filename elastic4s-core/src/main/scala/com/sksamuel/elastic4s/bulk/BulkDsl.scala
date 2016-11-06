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

  implicit object BulkDefinitionExecutable
    extends Executable[BulkDefinition, BulkResponse, BulkResult] {
    override def apply(c: Client, t: BulkDefinition): Future[BulkResult] = {
      injectFutureAndMap(c.bulk(t.build, _))(BulkResult.apply)
    }
  }
}

trait BulkCompatibleDefinition