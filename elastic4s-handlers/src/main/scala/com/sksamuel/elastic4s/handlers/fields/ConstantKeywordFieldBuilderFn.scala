package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.ConstantKeywordField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object ConstantKeywordFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): ConstantKeywordField = ConstantKeywordField(
    name,
    values.get("value").map(_.asInstanceOf[String]).get
  )

  def build(field: ConstantKeywordField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.field("value", field.value)
    builder.endObject()
  }
}
