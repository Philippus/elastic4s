package com.sksamuel.elastic4s.fields

object HistogramField {
  val `type`: String = "histogram"
}
case class HistogramField(name: String) extends ElasticField {
  override def `type`: String = HistogramField.`type`
}
