package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.PercolatorField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object PercolatorFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): PercolatorField = PercolatorField(name)

  def build(field: PercolatorField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
