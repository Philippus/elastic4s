package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.bulk.{BulkCompatibleRequest, BulkRequest}

trait BulkApi {
  this: IndexApi =>

  def bulk(requests: Iterable[BulkCompatibleRequest]): BulkRequest = BulkRequest(requests.toSeq)
  def bulk(requests: BulkCompatibleRequest*): BulkRequest          = bulk(requests)
}
