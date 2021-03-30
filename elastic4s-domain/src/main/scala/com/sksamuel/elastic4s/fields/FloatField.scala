package com.sksamuel.elastic4s.fields

case class FloatField(name: String,
                      boost: Option[Double] = None,
                      coerce: Option[Boolean] = None,
                      copyTo: Seq[String] = Nil,
                      docValues: Option[Boolean] = None,
                      ignoreMalformed: Option[Boolean] = None,
                      index: Option[Boolean] = None,
                      nullValue: Option[Float] = None,
                      store: Option[Boolean] = None,
                      meta: Map[String, Any] = Map.empty) extends NumberField[Float] {
  override def `type`: String = "float"
}
