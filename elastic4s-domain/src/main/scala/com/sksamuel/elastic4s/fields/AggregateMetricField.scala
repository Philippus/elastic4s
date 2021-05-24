package com.sksamuel.elastic4s.fields

case class AggregateMetricField(name: String, metrics: Seq[String], defaultMetric: String) extends ElasticField {
  override def `type`: String = "aggregate_metric_double"
}
