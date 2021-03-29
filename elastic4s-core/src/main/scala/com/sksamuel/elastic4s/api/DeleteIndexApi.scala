package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.indexes.DeleteIndexRequest

trait DeleteIndexApi {
  def deleteIndex(indexes: String*): DeleteIndexRequest          = deleteIndex(indexes)
  def deleteIndex(indexes: Iterable[String]): DeleteIndexRequest = DeleteIndexRequest(indexes.toSeq)
}
