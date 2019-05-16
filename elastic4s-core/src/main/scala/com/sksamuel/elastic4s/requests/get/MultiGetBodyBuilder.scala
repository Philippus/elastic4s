package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object MultiGetBodyBuilder {
  def apply(request: MultiGetRequest): XContentBuilder = {
    val builder = XContentFactory.obj()
    builder.startArray("docs")
    request.gets.foreach { get =>
      builder.startObject()
      builder.field("_index", get.index.index)
      builder.field("_id", get.id)
      get.routing.foreach(builder.field("routing", _))
      get.fetchSource.foreach { context =>
        if (context.includes.nonEmpty || context.excludes.nonEmpty) {
          builder.startObject("_source")
          if (context.includes.nonEmpty)
            builder.array("include", context.includes.toList)
          if (context.excludes.nonEmpty)
            builder.array("exclude", context.excludes.toList)
          builder.endObject()
        } else
          builder.field("_source", boolean = false)
      }
      if (get.storedFields.nonEmpty)
        builder.array("stored_fields", get.storedFields.toArray)
      builder.endObject()
    }
    builder.endArray()
    builder.endObject()
  }
}
