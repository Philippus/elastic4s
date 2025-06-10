package com.sksamuel.elastic4s.fields

object LongField {
  val `type`: String = "long"
}
case class LongField(
    name: String,
    boost: Option[Double] = None,
    coerce: Option[Boolean] = None,
    copyTo: Seq[String] = Nil,
    docValues: Option[Boolean] = None,
    ignoreMalformed: Option[Boolean] = None,
    index: Option[Boolean] = None,
    store: Option[Boolean] = None,
    nullValue: Option[Long] = None,
    meta: Map[String, String] = Map.empty,
    timeSeriesDimension: Option[Boolean] = None,
    timeSeriesMetric: Option[String] = None,
    fields: List[ElasticField] = Nil
) extends NumberField[Long] {
  override def `type`: String = LongField.`type`
}
