package com.sksamuel.elastic4s.fields

object FloatRangeField {
  val `type`: String = "float_range"
}
case class FloatRangeField(name: String,
                           boost: Option[Double] = None,
                           coerce: Option[Boolean] = None,
                           index: Option[Boolean] = None,
                           store: Option[Boolean] = None) extends RangeField {
  override def `type`: String = FloatRangeField.`type`
}
