package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.AliasField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object AliasFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): AliasField = {
    AliasField(name, values.get("path").map(_.asInstanceOf[String]).get)
  }

  def build(field: AliasField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.field("path", field.path)
    builder.endObject()
  }
}
