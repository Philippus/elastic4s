package com.sksamuel.elastic4s.index

import com.sksamuel.elastic4s.indexes.CreateIndexDefinition
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.common.xcontent.XContentType

object CreateIndexBuilder {
  def apply(d: CreateIndexDefinition): CreateIndexRequest = {
    d.rawSource match {
      case Some(s) => new CreateIndexRequest(d.name).source(s, XContentType.JSON)
      case None =>
        val source = CreateIndexContentBuilder(d)
        new CreateIndexRequest(d.name).source(source.bytes, XContentType.JSON)
    }
  }
}
