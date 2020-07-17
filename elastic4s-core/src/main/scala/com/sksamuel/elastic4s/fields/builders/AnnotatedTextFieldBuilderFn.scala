package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.AnnotatedTextField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object AnnotatedTextFieldBuilderFn {
  def build(field: AnnotatedTextField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
