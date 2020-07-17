package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.PercolatorField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object PercolatorFieldBuilderFn {
  def build(field: PercolatorField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
