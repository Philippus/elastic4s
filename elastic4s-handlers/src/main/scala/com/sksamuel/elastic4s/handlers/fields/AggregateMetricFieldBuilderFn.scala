package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.AggregateMetricField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object AggregateMetricFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): AggregateMetricField = {
    AggregateMetricField(
      name,
      values.get("metrics").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
      values.get("default_metric").map(_.asInstanceOf[String]).get
    )
  }

  def build(field: AggregateMetricField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    if (field.metrics.nonEmpty) builder.array("metrics", field.metrics.toArray)
    builder.field("default_metric", field.defaultMetric)
    builder.endObject()
  }
}
