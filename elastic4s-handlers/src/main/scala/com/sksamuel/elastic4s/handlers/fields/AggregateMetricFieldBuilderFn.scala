package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.AggregateMetricField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object AggregateMetricFieldBuilderFn {
  def build(field: AggregateMetricField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    if (field.metrics.nonEmpty) builder.array("metrics", field.metrics.toArray)
    builder.field("default_metric", field.defaultMetric)
    builder.endObject()
  }
}
