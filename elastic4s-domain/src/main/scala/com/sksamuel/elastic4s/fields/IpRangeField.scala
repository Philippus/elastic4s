package com.sksamuel.elastic4s.fields

object IpRangeField {
  val `type`: String = "ip_range"
}
case class IpRangeField(
    name: String,
    boost: Option[Double] = None,
    coerce: Option[Boolean] = None,
    index: Option[Boolean] = None,
    format: Option[String] = None,
    store: Option[Boolean] = None
) extends RangeField {
  override def `type`: String = "ip_range"
}
