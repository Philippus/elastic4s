package com.sksamuel.elastic4s.http.update

import com.sksamuel.elastic4s.FieldsMapper
import com.sksamuel.elastic4s.update.UpdateDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object UpdateContentBuilder {
  def apply(request: UpdateDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()

    request.documentSource.foreach { doc =>
      builder.rawField("doc", new BytesArray(doc), XContentType.JSON)
    }

    if (request.documentFields.nonEmpty) {
      builder.startObject("doc")
      request.documentFields.foreach { case (name, value) =>
        builder.field(name, FieldsMapper.mapper(value))
      }
      builder.endObject()
    }

    request.upsertSource.foreach { upsert =>
      builder.rawField("upsert", new BytesArray(upsert), XContentType.JSON)
    }

    if (request.upsertFields.nonEmpty) {
      builder.startObject("upsert")
      request.upsertFields.foreach { case (name, value) =>
        builder.field(name, FieldsMapper.mapper(value))
      }
      builder.endObject()
    }

    request.docAsUpsert.foreach(_ => builder.field("doc_as_upsert", true))
    request.scriptedUpsert.foreach(_ => builder.field("scripted_upsert", true))
    request.detectNoop.foreach(_ => builder.field("detect_noop", true))

    builder.endObject()
    builder
  }
}
