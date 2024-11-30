package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields._
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object NumberFieldBuilderFn {
  val supportedTypes = Set(
    ByteField.`type`,
    DoubleField.`type`,
    FloatField.`type`,
    HalfFloatField.`type`,
    ScaledFloatField.`type`,
    IntegerField.`type`,
    LongField.`type`,
    ShortField.`type`,
    UnsignedLongField.`type`
  )

  def toField(numberType: String, name: String, values: Map[String, Any]): NumberField[_] = numberType match {
    case ByteField.`type`         => ByteField(
        name,
        values.get("boost").map(_.asInstanceOf[Double]),
        values.get("coerce").map(_.asInstanceOf[Boolean]),
        values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
        values.get("doc_values").map(_.asInstanceOf[Boolean]),
        values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
        values.get("index").map(_.asInstanceOf[Boolean]),
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.byteValue()),
        values.get("store").map(_.asInstanceOf[Boolean])
      )
    case DoubleField.`type`       => DoubleField(
        name,
        values.get("boost").map(_.asInstanceOf[Double]),
        values.get("coerce").map(_.asInstanceOf[Boolean]),
        values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
        values.get("doc_values").map(_.asInstanceOf[Boolean]),
        values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
        values.get("index").map(_.asInstanceOf[Boolean]),
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.doubleValue()),
        values.get("store").map(_.asInstanceOf[Boolean])
      )
    case FloatField.`type`        => FloatField(
        name,
        values.get("boost").map(_.asInstanceOf[Double]),
        values.get("coerce").map(_.asInstanceOf[Boolean]),
        values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
        values.get("doc_values").map(_.asInstanceOf[Boolean]),
        values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
        values.get("index").map(_.asInstanceOf[Boolean]),
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.floatValue()),
        values.get("store").map(_.asInstanceOf[Boolean])
      )
    case HalfFloatField.`type`    => HalfFloatField(
        name,
        values.get("boost").map(_.asInstanceOf[Double]),
        values.get("coerce").map(_.asInstanceOf[Boolean]),
        values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
        values.get("doc_values").map(_.asInstanceOf[Boolean]),
        values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
        values.get("index").map(_.asInstanceOf[Boolean]),
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.floatValue()),
        values.get("store").map(_.asInstanceOf[Boolean])
      )
    case ScaledFloatField.`type`  => ScaledFloatField(
        name,
        values.get("boost").map(_.asInstanceOf[Double]),
        values.get("coerce").map(_.asInstanceOf[Boolean]),
        values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
        values.get("doc_values").map(_.asInstanceOf[Boolean]),
        values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
        values.get("scaling_factor").map(_.asInstanceOf[Int]),
        values.get("index").map(_.asInstanceOf[Boolean]),
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.floatValue()),
        values.get("store").map(_.asInstanceOf[Boolean])
      )
    case IntegerField.`type`      => IntegerField(
        name,
        values.get("boost").map(_.asInstanceOf[Double]),
        values.get("coerce").map(_.asInstanceOf[Boolean]),
        values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
        values.get("doc_values").map(_.asInstanceOf[Boolean]),
        values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
        values.get("index").map(_.asInstanceOf[Boolean]),
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.intValue()),
        values.get("store").map(_.asInstanceOf[Boolean])
      )
    case LongField.`type`         => LongField(
        name,
        values.get("boost").map(_.asInstanceOf[Double]),
        values.get("coerce").map(_.asInstanceOf[Boolean]),
        values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
        values.get("doc_values").map(_.asInstanceOf[Boolean]),
        values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
        values.get("index").map(_.asInstanceOf[Boolean]),
        values.get("store").map(_.asInstanceOf[Boolean]),
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.longValue())
      )
    case ShortField.`type`        => ShortField(
        name,
        values.get("boost").map(_.asInstanceOf[Double]),
        values.get("coerce").map(_.asInstanceOf[Boolean]),
        values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
        values.get("doc_values").map(_.asInstanceOf[Boolean]),
        values.get("enabled").map(_.asInstanceOf[Boolean]),
        values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
        values.get("index").map(_.asInstanceOf[Boolean]),
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.shortValue()),
        values.get("store").map(_.asInstanceOf[Boolean])
      )
    case UnsignedLongField.`type` => UnsignedLongField(
        name,
        values.get("boost").map(_.asInstanceOf[Double]),
        values.get("coerce").map(_.asInstanceOf[Boolean]),
        values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
        values.get("doc_values").map(_.asInstanceOf[Boolean]),
        values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
        values.get("index").map(_.asInstanceOf[Boolean]),
        values.get("store").map(_.asInstanceOf[Boolean]),
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.longValue())
      )
  }

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
      case v: Long   => builder.field("null_value", v)
      case v: Float  => builder.field("null_value", v)
      case v: Int    => builder.field("null_value", v)
      case v: Byte   => builder.field("null_value", v)
      case v: Short  => builder.field("null_value", v)
      case v: String => builder.field("null_value", v)
    }
    field.store.foreach(builder.field("store", _))
    field.coerce.foreach(builder.field("coerce", _))
    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))

    field match {
      case f: ScaledFloatField =>
        f.scalingFactor.foreach(builder.field("scaling_factor", _))
      case _                   =>
    }

    builder.endObject()
  }
}
