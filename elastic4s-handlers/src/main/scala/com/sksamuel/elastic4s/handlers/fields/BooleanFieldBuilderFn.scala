package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.BooleanField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object BooleanFieldBuilderFn {

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
