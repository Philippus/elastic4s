package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.IndexAndType

trait IndexDsl {
  def indexInto(index: String, `type`: String): IndexDefinition = indexInto(IndexAndType(index, `type`))
  def indexInto(indexType: IndexAndType): IndexDefinition = new IndexDefinition(indexType.index, indexType.`type`)
  def index(kv: (String, String)): IndexDefinition = new IndexDefinition(kv._1, kv._2)
}
