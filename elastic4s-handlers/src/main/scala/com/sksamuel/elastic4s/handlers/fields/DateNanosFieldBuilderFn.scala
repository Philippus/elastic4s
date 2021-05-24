package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.DateNanosField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object DateNanosFieldBuilderFn {

  def build(field: DateNanosField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.boost.foreach(builder.field("boost", _))
    if (field.copyTo.nonEmpty) builder.array("copy_to", field.copyTo.toArray)
    field.docValues.foreach(builder.field("doc_values", _))
    field.format.foreach(builder.field("format", _))
    field.locale.foreach(builder.field("locale", _))
    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
    field.index.foreach(builder.field("index", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.store.foreach(builder.field("store", _))

    builder.endObject()
  }
}
