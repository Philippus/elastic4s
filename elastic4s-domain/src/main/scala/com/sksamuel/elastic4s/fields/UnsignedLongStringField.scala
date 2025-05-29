package com.sksamuel.elastic4s.fields

object UnsignedLongStringField {
  val `type`: String = "unsigned_long"
}
case class UnsignedLongStringField(
    name: String,
    boost: Option[Double] = None,
    coerce: Option[Boolean] = None,
    copyTo: Seq[String] = Nil,
    docValues: Option[Boolean] = None,
    ignoreMalformed: Option[Boolean] = None,
    index: Option[Boolean] = None,
    store: Option[Boolean] = None,
    nullValue: Option[String] = None,
    meta: Map[String, String] = Map.empty,
    timeSeriesDimension: Option[Boolean] = None,
    timeSeriesMetric: Option[String] = None,
    fields: List[ElasticField] = Nil
) extends NumberField[String] {
  override def `type`: String = UnsignedLongStringField.`type`
}
