package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.AliasField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object AliasFieldBuilderFn {
  def build(field: AliasField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.path.foreach(builder.field("path", _))
    builder.endObject()
  }
}
