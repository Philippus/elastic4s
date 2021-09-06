package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.MatchOnlyTextField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object MatchOnlyTextFieldBuilderFn {
  def build(field: MatchOnlyTextField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    if (field.fields.nonEmpty) {
      builder.startObject("fields")
      field.fields.foreach { field =>
        builder.rawField(field.name, ElasticFieldBuilderFn(field))
      }
      builder.endObject()
    }

    builder.endObject()
  }
}
