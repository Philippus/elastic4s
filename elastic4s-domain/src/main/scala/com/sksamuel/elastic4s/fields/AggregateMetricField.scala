package com.sksamuel.elastic4s.fields

object AggregateMetricField {
  val `type` = "aggregate_metric_double"
}
case class AggregateMetricField(name: String, metrics: Seq[String], defaultMetric: String) extends ElasticField {
  override def `type`: String = AggregateMetricField.`type`
}
