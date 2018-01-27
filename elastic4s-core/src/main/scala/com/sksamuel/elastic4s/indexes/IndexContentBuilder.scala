package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.XContentFieldValueWriter
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object IndexContentBuilder {
  def apply(request: IndexDefinition): XContentBuilder =
    request.source match {
      case Some(json) => XContentFactory.parse(json)
      case None =>
        val source = XContentFactory.jsonBuilder()
        request.fields.foreach(XContentFieldValueWriter(source, _))
        source.endObject()
        source
    }
}
