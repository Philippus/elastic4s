package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.JoinField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object JoinFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): JoinField = JoinField(
    name,
    values.get("eager_global_ordinals").map(_.asInstanceOf[Boolean]),
    values.get("relations").map(_.asInstanceOf[Map[String, Any]]).getOrElse(Map.empty)
  )


  def build(field: JoinField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    builder.startObject("relations")
    field.relations.foreach {
      case (parent, child) =>
        builder.autofield(parent, child)
    }
    builder.endObject()

    field.eagerGlobalOrdinals.foreach(builder.field("eager_global_ordinals", _))

    builder.endObject()
  }
}
