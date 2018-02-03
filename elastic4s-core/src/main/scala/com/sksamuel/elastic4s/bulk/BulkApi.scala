package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.indexes.IndexApi

import scala.language.implicitConversions

trait BulkApi {
  this: IndexApi =>

  def bulk(requests: Iterable[BulkCompatibleRequest]): BulkRequest = BulkRequest(requests.toSeq)
  def bulk(requests: BulkCompatibleRequest*): BulkRequest          = bulk(requests)
}
