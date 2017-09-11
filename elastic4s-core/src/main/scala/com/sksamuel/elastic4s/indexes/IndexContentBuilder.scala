package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.XContentFieldValueWriter
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object IndexContentBuilder {
  def apply(request: IndexDefinition): XContentBuilder = {
    request.source match {
      case Some(json) => XContentFactory.jsonBuilder().rawValue(new BytesArray(json), XContentType.JSON)
      case None =>
        val source = XContentFactory.jsonBuilder().startObject()
        request.fields.foreach(XContentFieldValueWriter(source, _))
        source.endObject()
        source
    }
  }
}
