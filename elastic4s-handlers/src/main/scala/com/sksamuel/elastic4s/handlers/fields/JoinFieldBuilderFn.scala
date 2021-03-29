package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.JoinField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object JoinFieldBuilderFn {

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
