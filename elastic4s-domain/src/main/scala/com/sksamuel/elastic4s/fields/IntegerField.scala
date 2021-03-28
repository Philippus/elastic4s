package com.sksamuel.elastic4s.fields

case class IntegerField(name: String,
                        boost: Option[Double] = None,
                        coerce: Option[Boolean] = None,
                        copyTo: Seq[String] = Nil,
                        docValues: Option[Boolean] = None,
                        ignoreMalformed: Option[Boolean] = None,
                        index: Option[Boolean] = None,
                        nullValue: Option[Int] = None,
                        store: Option[Boolean] = None,
                        meta: Map[String, Any] = Map.empty) extends NumberField[Int] {
  override def `type`: String = "integer"
}
