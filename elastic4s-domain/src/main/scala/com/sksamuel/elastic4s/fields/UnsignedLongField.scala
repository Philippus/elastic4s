package com.sksamuel.elastic4s.fields

object UnsignedLongField {
  val `type`: String = "unsigned_long"
}
case class UnsignedLongField(name: String,
                             boost: Option[Double] = None,
                             coerce: Option[Boolean] = None,
                             copyTo: Seq[String] = Nil,
                             docValues: Option[Boolean] = None,
                             ignoreMalformed: Option[Boolean] = None,
                             index: Option[Boolean] = None,
                             store: Option[Boolean] = None,
                             nullValue: Option[Long] = None,
                             meta: Map[String, Any] = Map.empty) extends NumberField[Long] {
  override def `type`: String = UnsignedLongField.`type`
}
