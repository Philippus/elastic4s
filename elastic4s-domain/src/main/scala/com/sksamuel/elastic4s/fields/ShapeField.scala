package com.sksamuel.elastic4s.fields

object ShapeField {
  val `type`: String = "shape"
}
case class ShapeField(name: String,
                      boost: Option[Double] = None,
                      coerce: Option[Boolean] = None,
                      copyTo: Seq[String] = Nil,
                      ignoreMalformed: Option[Boolean] = None,
                      ignoreZValue: Option[Boolean] = None,
                      index: Option[Boolean] = None,
                      norms: Option[Boolean] = None,
                      nullValue: Option[String] = None,
                      store: Option[Boolean] = None,
                      orientation: Option[String] = None,
                      meta: Map[String, Any] = Map.empty) extends ElasticField {
  override def `type`: String = ShapeField.`type`
}
