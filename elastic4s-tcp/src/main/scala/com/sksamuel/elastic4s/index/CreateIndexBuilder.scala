package com.sksamuel.elastic4s.index

import com.sksamuel.elastic4s.indexes.{CreateIndexContentBuilder, CreateIndexDefinition}
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest

object CreateIndexBuilder {
  def apply(d: CreateIndexDefinition): CreateIndexRequest = {
    d.rawSource match {
      case Some(s) => new CreateIndexRequest(d.name).source(s)
      case None =>
        val source = CreateIndexContentBuilder(d)
        new CreateIndexRequest(d.name).source(source)
    }
  }
}
