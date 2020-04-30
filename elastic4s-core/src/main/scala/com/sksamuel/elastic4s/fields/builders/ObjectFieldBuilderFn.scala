package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.ObjectField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object ObjectFieldBuilderFn {

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
