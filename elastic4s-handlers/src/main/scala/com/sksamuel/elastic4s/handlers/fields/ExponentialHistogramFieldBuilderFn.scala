package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.ExponentialHistogramField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object ExponentialHistogramFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): ExponentialHistogramField = ExponentialHistogramField(name)

  def build(field: ExponentialHistogramField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.endObject()
  }
}
