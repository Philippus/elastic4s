package com.sksamuel.elastic4s.fields

object DateRangeField {
  val `type`: String = "date_range"
}
case class DateRangeField(name: String,
                          boost: Option[Double] = None,
                          coerce: Option[Boolean] = None,
                          index: Option[Boolean] = None,
                          format: Option[String] = None,
                          store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = DateRangeField.`type`
}
