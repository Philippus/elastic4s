package com.sksamuel.elastic4s.fields

object ShortField {
  val `type`: String = "short"
}
case class ShortField(
    name: String,
    boost: Option[Double] = None,
    coerce: Option[Boolean] = None,
    copyTo: Seq[String] = Nil,
    docValues: Option[Boolean] = None,
    enabled: Option[Boolean] = None,
    ignoreMalformed: Option[Boolean] = None,
    index: Option[Boolean] = None,
    nullValue: Option[Short] = None,
    store: Option[Boolean] = None,
    meta: Map[String, Any] = Map.empty
) extends NumberField[Short] {
  override def `type`: String = ShortField.`type`
}
