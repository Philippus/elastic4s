package com.sksamuel.elastic4s.handlers.index

import com.sksamuel.elastic4s.json.{XContentFactory, XContentFieldValueWriter}
import com.sksamuel.elastic4s.requests.indexes.IndexRequest

object IndexContentBuilder {
  def apply(request: IndexRequest): String =
    request.source match {
      case Some(json) => json
      case None       =>
        val source = XContentFactory.jsonBuilder()
        request.fields.foreach(XContentFieldValueWriter(source, _))
        source.endObject().string
    }
}
