package com.sksamuel.elastic4s.fields

object LongRangeField {
  val `type`: String = "long_range"
}
case class LongRangeField(
    name: String,
    boost: Option[Double] = None,
    coerce: Option[Boolean] = None,
    index: Option[Boolean] = None,
    store: Option[Boolean] = None
) extends RangeField {
  override def `type`: String = LongRangeField.`type`
}
