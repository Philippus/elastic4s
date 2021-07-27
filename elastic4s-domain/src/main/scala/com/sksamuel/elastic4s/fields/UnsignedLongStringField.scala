package com.sksamuel.elastic4s.fields

case class UnsignedLongStringField(name: String,
                                    boost: Option[Double] = None,
                                    coerce: Option[Boolean] = None,
                                    copyTo: Seq[String] = Nil,
                                    docValues: Option[Boolean] = None,
                                    ignoreMalformed: Option[Boolean] = None,
                                    index: Option[Boolean] = None,
                                    store: Option[Boolean] = None,
                                    nullValue: Option[String] = None,
                                    meta: Map[String, Any] = Map.empty) extends NumberField[String] {
  override def `type`: String = "unsigned_long"
}
