package com.sksamuel.elastic4s.indexes

trait DeleteIndexApi {
  def deleteIndex(indexes: String*): DeleteIndex          = deleteIndex(indexes)
  def deleteIndex(indexes: Iterable[String]): DeleteIndex = DeleteIndex(indexes.toSeq)
}
