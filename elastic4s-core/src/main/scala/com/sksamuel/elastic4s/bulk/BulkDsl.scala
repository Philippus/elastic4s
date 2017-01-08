package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.indexes.IndexDsl

import scala.language.implicitConversions

trait BulkDsl {
  this: IndexDsl =>

  def bulk(requests: Iterable[BulkCompatibleDefinition]): BulkDefinition = BulkDefinition(requests.toSeq)
  def bulk(requests: BulkCompatibleDefinition*): BulkDefinition = bulk(requests)
}
