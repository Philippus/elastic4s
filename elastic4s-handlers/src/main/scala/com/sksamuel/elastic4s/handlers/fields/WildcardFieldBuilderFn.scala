package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.WildcardField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object WildcardFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): WildcardField = WildcardField(
    name,
    values.get("ignore_above").map(_.asInstanceOf[Int]),
    values.get("null_value").map(_.asInstanceOf[String])
  )

  def build(field: WildcardField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.ignoreAbove.foreach(builder.field("ignore_above", _))
    field.nullValue.foreach(builder.field("null_value", _))
    builder.endObject()
  }
}
