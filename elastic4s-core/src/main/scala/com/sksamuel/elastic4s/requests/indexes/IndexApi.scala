package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.{Index, IndexAndType}

trait IndexApi {

  def indexInto(index: String, `type`: String): IndexRequest = indexInto(IndexAndType(index, `type`))

  def indexInto(index: Index, `type`: String): IndexRequest = indexInto(IndexAndType(index.name, `type`))

  def indexInto(indexType: IndexAndType): IndexRequest = IndexRequest(indexType)

  def indexInto(index: String): IndexRequest = IndexRequest(index)

  def index(kv: (String, String)): IndexRequest = IndexRequest(IndexAndType(kv._1, kv._2))

  def getIndex(index: String, others: String*): GetIndexRequest = getIndex(index +: others)
  def getIndex(indexes: Seq[String]): GetIndexRequest           = GetIndexRequest(indexes.mkString(","))
}
