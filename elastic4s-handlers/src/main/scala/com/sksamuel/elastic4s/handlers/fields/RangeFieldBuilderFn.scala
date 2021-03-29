package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.{DateRangeField, RangeField}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object RangeFieldBuilderFn {

  def build(field: RangeField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.boost.foreach(builder.field("boost", _))
    field.index.foreach(builder.field("index", _))
    field.store.foreach(builder.field("store", _))
    field.coerce.foreach(builder.field("coerce", _))

    field match {
      case f: DateRangeField =>
        f.format.foreach(builder.field("format", _))
      case _ =>
    }

    builder.endObject()
  }
}
