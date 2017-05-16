package com.sksamuel.elastic4s.http.get

import com.sksamuel.elastic4s.get.MultiGetDefinition
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object MultiGetBodyBuilder {
  def apply(request: MultiGetDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startArray("docs")
    request.gets.foreach { get =>
      builder.startObject()
      builder.field("_index", get.indexAndType.index)
      builder.field("_type", get.indexAndType.`type`)
      builder.field("_id", get.id)
      get.fetchSource.foreach { context =>
        if (context.includes.nonEmpty || context.excludes.nonEmpty) {
          builder.startObject("_source")
          if (context.includes.nonEmpty)
            builder.field("include", context.includes)
          if (context.excludes.nonEmpty)
            builder.field("exclude", context.excludes)
          builder.endObject()
        } else {
          builder.field("_source", false)
        }
      }
      if (get.storedFields.nonEmpty) {
        builder.field("stored_fields", get.storedFields.toArray)
      }
      builder.endObject()
    }
    builder.endArray()
    builder.endObject()
    builder
  }
}
