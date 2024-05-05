package com.sksamuel.elastic4s.handlers.update

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.update.UpdateRequest
import com.sksamuel.elastic4s.{FieldsMapper, handlers}

object UpdateBuilderFn {
  def apply(request: UpdateRequest): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    request.documentSource.foreach { doc =>
      builder.rawField("doc", doc)
    }

    request.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
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

    if (request.upsertFields.nonEmpty || request.scriptedUpsert.contains(true)) {
      builder.startObject("upsert")
      request.upsertFields.foreach {
        case (name, value) =>
          builder.autofield(name, FieldsMapper.mapper(value))
      }
      builder.endObject()
    }

    request.docAsUpsert.foreach(docAsUpsert => builder.field("doc_as_upsert", docAsUpsert))
    request.scriptedUpsert.foreach(scriptedUpsert => builder.field("scripted_upsert", scriptedUpsert))
    request.detectNoop.foreach(detectNoop => builder.field("detect_noop", detectNoop))

    builder.endObject()
    builder
  }
}
