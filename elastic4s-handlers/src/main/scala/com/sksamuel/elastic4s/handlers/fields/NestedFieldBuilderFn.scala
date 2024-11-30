package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.NestedField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object NestedFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): NestedField = NestedField(
    name,
    values.get("dynamic").map(_.asInstanceOf[String]),
    values.get("enabled").map(_.asInstanceOf[Boolean]),
    values
      .get("properties")
      .map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
        ElasticFieldBuilderFn.construct(key, value)
      }.toSeq)
      .getOrElse(Seq.empty),
    values.get("include_in_parent").map(_.asInstanceOf[Boolean]),
    values.get("include_in_root").map(_.asInstanceOf[Boolean])
  )

  def build(field: NestedField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.enabled.foreach(builder.field("enabled", _))
    field.dynamic.foreach(builder.field("dynamic", _))
    field.includeInParent.foreach(builder.field("include_in_parent", _))
    field.includeInRoot.foreach(builder.field("include_in_root", _))

    if (field.properties.nonEmpty) {
      builder.startObject("properties")
      field.properties.foreach { property =>
        builder.rawField(property.name, ElasticFieldBuilderFn(property))
      }
      builder.endObject()
    }

    builder.endObject()
  }
}
