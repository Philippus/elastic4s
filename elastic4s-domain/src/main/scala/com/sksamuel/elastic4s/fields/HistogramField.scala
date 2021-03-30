package com.sksamuel.elastic4s.fields

case class HistogramField(name: String) extends ElasticField {
  override def `type`: String = "histogram"
}
