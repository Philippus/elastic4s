package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.BooleanField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object BooleanFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): BooleanField =
    BooleanField(
      name,
      values.get("boost").map(_.asInstanceOf[Double]),
      values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
      values.get("doc_values").map(_.asInstanceOf[Boolean]),
      values.get("index").map(_.asInstanceOf[Boolean]),
      values.get("null_value").map(_.asInstanceOf[Boolean]),
      values.get("store").map(_.asInstanceOf[Boolean]),
      values.get("meta").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty),
      values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
      values.get("time_series_dimension").map(_.asInstanceOf[Boolean]),
      values.get("fields").map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
        ElasticFieldBuilderFn.construct(key, value)
      }.toList).getOrElse(List.empty)
    )

  def build(field: BooleanField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.boost.foreach(builder.field("boost", _))
    if (field.copyTo.nonEmpty) builder.array("copy_to", field.copyTo.toArray)
    field.docValues.foreach(builder.field("doc_values", _))
    field.index.foreach(builder.field("index", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.store.foreach(builder.field("store", _))

    if (field.meta.nonEmpty) {
      builder.startObject("meta")
      field.meta.foreach { case (key, value) => builder.autofield(key, value) }
      builder.endObject()
    }

    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
    field.timeSeriesDimension.foreach(builder.field("time_series_dimension", _))

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
