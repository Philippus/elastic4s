package com.sksamuel.elastic4s.fields

case class DateRangeField(name: String,
                          boost: Option[Double] = None,
                          coerce: Option[Boolean] = None,
                          index: Option[Boolean] = None,
                          format: Option[String] = None,
                          store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = "date_range"
}
