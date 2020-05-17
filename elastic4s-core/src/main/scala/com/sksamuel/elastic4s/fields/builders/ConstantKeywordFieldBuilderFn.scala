package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.{ConstantKeywordField, DateField}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object ConstantKeywordFieldBuilderFn {

  def build(field: ConstantKeywordField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.field("value", field.value)
    builder.endObject()
  }
}
