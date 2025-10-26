package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.PatternTextField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object PatternTextFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): PatternTextField = PatternTextField(
    name,
    values.get("analyzer").map(_.asInstanceOf[String]),
    values.get("index_options").map(_.asInstanceOf[String]),
    values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty)
  )

  def build(field: PatternTextField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.analyzer.foreach(builder.field("analyzer", _))
    field.indexOptions.foreach(builder.field("index_options", _))
    if (field.meta.nonEmpty) {
      builder.startObject("meta")
      field.meta.foreach { case (key, value) => builder.autofield(key, value) }
      builder.endObject()
    }
    builder.endObject()
  }
}
