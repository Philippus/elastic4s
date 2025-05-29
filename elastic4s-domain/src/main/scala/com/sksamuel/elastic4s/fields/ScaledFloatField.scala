package com.sksamuel.elastic4s.fields

object ScaledFloatField {
  val `type`: String = "scaled_float"
}
case class ScaledFloatField(
    name: String,
    boost: Option[Double] = None,
    coerce: Option[Boolean] = None,
    copyTo: Seq[String] = Nil,
    docValues: Option[Boolean] = None,
    ignoreMalformed: Option[Boolean] = None,
    scalingFactor: Option[Int] = None,
    index: Option[Boolean] = None,
    nullValue: Option[Float] = None,
    store: Option[Boolean] = None,
    meta: Map[String, String] = Map.empty,
    timeSeriesMetric: Option[String] = None
) extends NumberField[Float] {
  override def `type`: String = "scaled_float"
}
