package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.{DateRangeField, DoubleRangeField, FloatRangeField, IntegerRangeField, IpRangeField, LongRangeField, RangeField}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object RangeFieldBuilderFn {
  val supportedTypes = Set(
    DateRangeField.`type`,
    DoubleRangeField.`type`,
    FloatRangeField.`type`,
    IntegerRangeField.`type`,
    LongRangeField.`type`
  )

  def toField(`type`: String, name: String, values: Map[String, Any]): RangeField = `type` match {
    case DateRangeField.`type` => DateRangeField(
      name,
      values.get("boost").map(_.asInstanceOf[Double]),
      values.get("coerce").map(_.asInstanceOf[Boolean]),
      values.get("index").map(_.asInstanceOf[Boolean]),
      values.get("format").map(_.asInstanceOf[String]),
      values.get("store").map(_.asInstanceOf[Boolean]),
    )
    case DoubleRangeField.`type` => DoubleRangeField(
      name,
      values.get("boost").map(_.asInstanceOf[Double]),
      values.get("coerce").map(_.asInstanceOf[Boolean]),
      values.get("index").map(_.asInstanceOf[Boolean]),
      values.get("store").map(_.asInstanceOf[Boolean]),
    )
    case FloatRangeField.`type` => FloatRangeField(
      name,
      values.get("boost").map(_.asInstanceOf[Double]),
      values.get("coerce").map(_.asInstanceOf[Boolean]),
      values.get("index").map(_.asInstanceOf[Boolean]),
      values.get("store").map(_.asInstanceOf[Boolean]),
    )
    case IntegerRangeField.`type` => IntegerRangeField(
      name,
      values.get("boost").map(_.asInstanceOf[Double]),
      values.get("coerce").map(_.asInstanceOf[Boolean]),
      values.get("index").map(_.asInstanceOf[Boolean]),
      values.get("store").map(_.asInstanceOf[Boolean]),
    )
    case LongRangeField.`type` => LongRangeField(
      name,
      values.get("boost").map(_.asInstanceOf[Double]),
      values.get("coerce").map(_.asInstanceOf[Boolean]),
      values.get("index").map(_.asInstanceOf[Boolean]),
      values.get("store").map(_.asInstanceOf[Boolean]),
    )
  }


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
