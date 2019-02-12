package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{FieldsMapper, XContentBuilder, XContentFactory}

object UpdateBuilderFn {
  def apply(request: UpdateRequest): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    request.documentSource.foreach { doc =>
      builder.rawField("doc", doc)
    }

    request.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }

    if (request.documentFields.nonEmpty) {
      builder.startObject("doc")
      request.documentFields.foreach {
        case (name, value) =>
          builder.autofield(name, FieldsMapper.mapper(value))
      }
      builder.endObject()
    }

    request.upsertSource.foreach { upsert =>
      builder.rawField("upsert", upsert)
    }

    if (request.upsertFields.nonEmpty) {
      builder.startObject("upsert")
      request.upsertFields.foreach {
        case (name, value) =>
          builder.autofield(name, FieldsMapper.mapper(value))
      }
      builder.endObject()
    }

    request.docAsUpsert.foreach(_ => builder.field("doc_as_upsert", true))
    request.scriptedUpsert.foreach(_ => builder.field("scripted_upsert", true))
    request.detectNoop.foreach(detectNoop => builder.field("detect_noop", detectNoop))

    builder.endObject()
    builder
  }
}
