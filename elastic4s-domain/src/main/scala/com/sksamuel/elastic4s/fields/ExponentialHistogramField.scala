package com.sksamuel.elastic4s.fields

object ExponentialHistogramField {
  val `type`: String = "exponential_histogram"
}
case class ExponentialHistogramField(name: String) extends ElasticField {
  override def `type`: String = ExponentialHistogramField.`type`
}
