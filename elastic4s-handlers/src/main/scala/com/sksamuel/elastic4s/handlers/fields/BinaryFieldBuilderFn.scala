package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.BinaryField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object BinaryFieldBuilderFn {
  def build(field: BinaryField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.docValues.foreach(builder.field("doc_values", _))
    field.store.foreach(builder.field("store", _))
    builder.endObject()
  }
}
