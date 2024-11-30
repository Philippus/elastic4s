package com.sksamuel.elastic4s.fields

object IntegerRangeField {
  val `type`: String = "integer_range"
}
case class IntegerRangeField(
    name: String,
    boost: Option[Double] = None,
    coerce: Option[Boolean] = None,
    index: Option[Boolean] = None,
    store: Option[Boolean] = None
) extends RangeField {
  override def `type`: String = IntegerRangeField.`type`
}
