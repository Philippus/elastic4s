package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.Murmur3Field
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object Murmur3FieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): Murmur3Field = Murmur3Field(name)

  def build(field: Murmur3Field): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
