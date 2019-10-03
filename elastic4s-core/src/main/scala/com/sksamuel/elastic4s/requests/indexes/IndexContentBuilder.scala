package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.{XContentFactory, XContentFieldValueWriter}

object IndexContentBuilder {
  def apply(request: IndexRequest): String =
    request.source match {
      case Some(json) => json
      case None =>
        val source = XContentFactory.jsonBuilder()
        request.fields.foreach(XContentFieldValueWriter(source, _))
        source.endObject().string()
    }
}
