package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.{IpField, IpRangeField}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object IpFieldBuilderFn {

  def build(field: IpField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.boost.foreach(builder.field("boost", _))
    field.docValues.foreach(builder.field("doc_values", _))
    field.index.foreach(builder.field("index", _))
    field.docValues.foreach(builder.field("doc_values", _))
    field.store.foreach(builder.field("store", _))
    builder.endObject()
  }
}

object IpRangeFieldBuilderFn {

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

