package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.Index

trait IndexApi {
  def indexInto(index: Index): IndexRequest = IndexRequest(index)

  def getIndex(index: String, others: String*): GetIndexRequest = getIndex(index +: others)
  def getIndex(indexes: Seq[String]): GetIndexRequest           = GetIndexRequest(indexes.mkString(","))
}
