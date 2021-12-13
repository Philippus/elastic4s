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
      values.get("store").map(_.asInstanceOf[Boolean])
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

    builder.endObject()
  }
}
