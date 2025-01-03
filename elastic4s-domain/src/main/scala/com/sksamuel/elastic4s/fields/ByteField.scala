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
    meta: Map[String, Any] = Map.empty
) extends NumberField[Byte] {
  override def `type`: String = ByteField.`type`
}
