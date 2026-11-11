package com.sksamuel.elastic4s.handlers.get

import com.sksamuel.elastic4s.handlers.common.FetchSourceContextBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.get.MultiGetRequest

object MultiGetBodyBuilder {
  def apply(request: MultiGetRequest): XContentBuilder = {
    val builder = XContentFactory.obj()
    builder.startArray("docs")
    request.gets.foreach { get =>
      builder.startObject()
      builder.field("_index", get.index.index)
      builder.field("_id", get.id)
      get.routing.foreach(builder.field("routing", _))
      get.fetchSource.foreach(FetchSourceContextBuilderFn(builder, _))
      if (get.storedFields.nonEmpty)
        builder.array("stored_fields", get.storedFields.toArray)
      builder.endObject()
    }
    builder.endArray()
    builder.endObject()
  }
}
