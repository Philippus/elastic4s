package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.HistogramField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object HistogramFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): HistogramField = HistogramField(name)

  def build(field: HistogramField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
