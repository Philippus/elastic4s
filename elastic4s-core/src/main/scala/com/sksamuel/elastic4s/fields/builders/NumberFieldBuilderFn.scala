package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.{NumberField, ScaledFloatField}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object NumberFieldBuilderFn {

  def build(field: NumberField[_]): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    if (field.copyTo.nonEmpty)
      builder.array("copy_to", field.copyTo.toArray)

    field.boost.foreach(builder.field("boost", _))
    field.docValues.foreach(builder.field("doc_values", _))
    field.index.foreach(builder.field("index", _))
    field.nullValue.foreach {
      case v: Double => builder.field("null_value", v)
      case v: Long => builder.field("null_value", v)
      case v: Float => builder.field("null_value", v)
      case v: Int => builder.field("null_value", v)
      case v: Byte => builder.field("null_value", v)
      case v: Short => builder.field("null_value", v)
    }
    field.store.foreach(builder.field("store", _))
    field.coerce.foreach(builder.field("coerce", _))
    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))

    field match {
      case f: ScaledFloatField =>
        f.scalingFactor.foreach(builder.field("scaling_factor", _))
      case _ =>
    }

    builder.endObject()
  }
}
