package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.VersionField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object VersionFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): VersionField = VersionField(name)

  def build(field: VersionField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
