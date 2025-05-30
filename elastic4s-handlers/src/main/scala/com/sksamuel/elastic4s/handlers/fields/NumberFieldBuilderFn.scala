package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields._
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object NumberFieldBuilderFn {
  private[fields] val supportedTypes = Set(
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
        values.get("store").map(_.asInstanceOf[Boolean]),
        values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
        values.get("time_series_dimension").map(_.asInstanceOf[Boolean]),
        values.get("time_series_metric").map(_.asInstanceOf[String]),
        values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
          ElasticFieldBuilderFn.construct(key, value)
        }.toList).getOrElse(List.empty)
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
        values.get("store").map(_.asInstanceOf[Boolean]),
        values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
        values.get("time_series_metric").map(_.asInstanceOf[String]),
        values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
          ElasticFieldBuilderFn.construct(key, value)
        }.toList).getOrElse(List.empty)
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
        values.get("store").map(_.asInstanceOf[Boolean]),
        values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
        values.get("time_series_metric").map(_.asInstanceOf[String]),
        values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
          ElasticFieldBuilderFn.construct(key, value)
        }.toList).getOrElse(List.empty)
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
        values.get("store").map(_.asInstanceOf[Boolean]),
        values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
        values.get("time_series_metric").map(_.asInstanceOf[String]),
        values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
          ElasticFieldBuilderFn.construct(key, value)
        }.toList).getOrElse(List.empty)
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
        values.get("store").map(_.asInstanceOf[Boolean]),
        values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
        values.get("time_series_metric").map(_.asInstanceOf[String]),
        values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
          ElasticFieldBuilderFn.construct(key, value)
        }.toList).getOrElse(List.empty)
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
        values.get("store").map(_.asInstanceOf[Boolean]),
        values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
        values.get("time_series_dimension").map(_.asInstanceOf[Boolean]),
        values.get("time_series_metric").map(_.asInstanceOf[String]),
        values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
          ElasticFieldBuilderFn.construct(key, value)
        }.toList).getOrElse(List.empty)
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
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.longValue()),
        values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
        values.get("time_series_dimension").map(_.asInstanceOf[Boolean]),
        values.get("time_series_metric").map(_.asInstanceOf[String]),
        values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
          ElasticFieldBuilderFn.construct(key, value)
        }.toList).getOrElse(List.empty)
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
        values.get("store").map(_.asInstanceOf[Boolean]),
        values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
        values.get("time_series_dimension").map(_.asInstanceOf[Boolean]),
        values.get("time_series_metric").map(_.asInstanceOf[String]),
        values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
          ElasticFieldBuilderFn.construct(key, value)
        }.toList).getOrElse(List.empty)
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
        values.get("null_value").map(_.asInstanceOf[Number]).map(_.longValue()),
        values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
        values.get("time_series_dimension").map(_.asInstanceOf[Boolean]),
        values.get("time_series_metric").map(_.asInstanceOf[String]),
        values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
          ElasticFieldBuilderFn.construct(key, value)
        }.toList).getOrElse(List.empty)
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

    if (field.meta.nonEmpty) {
      builder.startObject("meta")
      field.meta.foreach { case (key, value) => builder.autofield(key, value) }
      builder.endObject()
    }

    field match {
      case f: ByteField         =>
        f.timeSeriesDimension.foreach(builder.field("time_series_dimension", _))
      case f: ShortField        =>
        f.timeSeriesDimension.foreach(builder.field("time_series_dimension", _))
      case f: IntegerField      =>
        f.timeSeriesDimension.foreach(builder.field("time_series_dimension", _))
      case f: LongField         =>
        f.timeSeriesDimension.foreach(builder.field("time_series_dimension", _))
      case f: UnsignedLongField =>
        f.timeSeriesDimension.foreach(builder.field("time_series_dimension", _))
      case _                    =>
    }

    field.timeSeriesMetric.foreach(builder.field("time_series_metric", _))

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
