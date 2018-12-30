package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory, XContentFieldValueWriter}

object IndexContentBuilder {
  def apply(request: IndexRequest): XContentBuilder =
    request.source match {
      case Some(json) => XContentFactory.parse(json)
      case None =>
        val source = XContentFactory.jsonBuilder()
        request.fields.foreach(XContentFieldValueWriter(source, _))
        source.endObject()
        source
    }
}
