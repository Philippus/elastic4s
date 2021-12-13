package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.MatchOnlyTextField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object MatchOnlyTextFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): MatchOnlyTextField = MatchOnlyTextField(
    name,
    values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) => ElasticFieldBuilderFn.construct(key, value) }.toList).getOrElse(List.empty),
  )

  def build(field: MatchOnlyTextField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    if (field.fields.nonEmpty) {
      builder.startObject("fields")
      field.fields.foreach { field =>
        builder.rawField(field.name, ElasticFieldBuilderFn(field))
      }
      builder.endObject()
    }

    builder.endObject()
  }
}
