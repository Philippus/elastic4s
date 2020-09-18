package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.WildcardField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object WildcardFieldBuilderFn {

  def build(field: WildcardField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.ignoreAbove.foreach(builder.field("ignore_above", _))
    builder.endObject()
  }
}
