package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.GeoPointField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object GeoPointFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): GeoPointField = GeoPointField(
    name,
    values.get("boost").map(_.asInstanceOf[Double]),
    values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
    values.get("doc_values").map(_.asInstanceOf[Boolean]),
    values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
    values.get("ignore_z_value").map(_.asInstanceOf[Boolean]),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("norms").map(_.asInstanceOf[Boolean]),
    values.get("null_value").map(_.asInstanceOf[String]),
    values.get("store").map(_.asInstanceOf[Boolean])
  )

  def build(field: GeoPointField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.boost.foreach(builder.field("boost", _))
    if (field.copyTo.nonEmpty) builder.array("copy_to", field.copyTo.toArray)
    field.docValues.foreach(builder.field("doc_values", _))
    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
    field.ignoreZValue.foreach(builder.field("ignore_z_value", _))
    field.index.foreach(builder.field("index", _))
    field.norms.foreach(builder.field("norms", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.store.foreach(builder.field("store", _))

    builder.endObject()
  }
}
