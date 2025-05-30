package com.sksamuel.elastic4s.fields

object ByteField {
  val `type`: String = "byte"
}
case class ByteField(
    name: String,
    boost: Option[Double] = None,
    coerce: Option[Boolean] = None,
    copyTo: Seq[String] = Nil,
    docValues: Option[Boolean] = None,
    ignoreMalformed: Option[Boolean] = None,
    index: Option[Boolean] = None,
    nullValue: Option[Byte] = None,
    store: Option[Boolean] = None,
    meta: Map[String, String] = Map.empty,
    timeSeriesDimension: Option[Boolean] = None,
    timeSeriesMetric: Option[String] = None,
    fields: List[ElasticField] = Nil
) extends NumberField[Byte] {
  override def `type`: String = ByteField.`type`
}
