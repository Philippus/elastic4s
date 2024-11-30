package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.{IpField, IpRangeField}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object IpFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): IpField = IpField(
    name,
    values.get("boost").map(_.asInstanceOf[Double]),
    values.get("doc_values").map(_.asInstanceOf[Boolean]),
    values.get("ignore_malformed").map(_.asInstanceOf[Boolean]),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("null_value").map(_.asInstanceOf[String]),
    values.get("store").map(_.asInstanceOf[Boolean])
  )

  def build(field: IpField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.boost.foreach(builder.field("boost", _))
    field.docValues.foreach(builder.field("doc_values", _))
    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
    field.index.foreach(builder.field("index", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.store.foreach(builder.field("store", _))
    builder.endObject()
  }
}

object IpRangeFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): IpRangeField = IpRangeField(
    name,
    values.get("boost").map(_.asInstanceOf[Double]),
    values.get("coerce").map(_.asInstanceOf[Boolean]),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("format").map(_.asInstanceOf[String]),
    values.get("store").map(_.asInstanceOf[Boolean])
  )

  def build(field: IpRangeField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.boost.foreach(builder.field("boost", _))
    field.index.foreach(builder.field("index", _))
    field.format.foreach(builder.field("format", _))
    field.coerce.foreach(builder.field("coerce", _))
    field.store.foreach(builder.field("store", _))
    builder.endObject()
  }
}
