package com.sksamuel.elastic4s.indexes

trait DeleteIndexApi {
  def deleteIndex(indexes: String*): DeleteIndexRequest          = deleteIndex(indexes)
  def deleteIndex(indexes: Iterable[String]): DeleteIndexRequest = DeleteIndexRequest(indexes.toSeq)
}
