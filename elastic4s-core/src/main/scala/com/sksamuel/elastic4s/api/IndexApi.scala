package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.requests.indexes.{GetIndexRequest, IndexRequest}

trait IndexApi {
  def indexInto(index: Index): IndexRequest = IndexRequest(index)

  def getIndex(index: String, others: String*): GetIndexRequest = getIndex(index +: others)
  def getIndex(indexes: Seq[String]): GetIndexRequest = GetIndexRequest(indexes.mkString(","))
}
