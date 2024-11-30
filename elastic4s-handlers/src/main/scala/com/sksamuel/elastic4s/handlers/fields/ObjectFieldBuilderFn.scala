package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.ObjectField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object ObjectFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): ObjectField = ObjectField(
    name,
    values.get("dynamic").map(_.asInstanceOf[String]),
    values.get("enabled").map(_.asInstanceOf[Boolean]),
    values
      .get("properties")
      .map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
        ElasticFieldBuilderFn.construct(key, value)
      }.toSeq)
      .getOrElse(Seq.empty)
  )

  def build(field: ObjectField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.enabled.foreach(builder.field("enabled", _))
    field.dynamic.foreach(builder.field("dynamic", _))

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
