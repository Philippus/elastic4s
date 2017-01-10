package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.IndexAndType

trait IndexApi {
  def indexInto(index: String, `type`: String): IndexDefinition = indexInto(IndexAndType(index, `type`))
  def indexInto(indexType: IndexAndType): IndexDefinition = IndexDefinition(indexType)
  def index(kv: (String, String)): IndexDefinition = IndexDefinition(IndexAndType(kv._1, kv._2))
}
