package com.sksamuel.elastic4s.fields

object ObjectField {
  val `type`: String = "object"
}
case class ObjectField(
    name: String,
    dynamic: Option[String] = None,
    enabled: Option[Boolean] = None,
    properties: Seq[ElasticField] = Nil
) extends ElasticField {
  override def `type`: String = ObjectField.`type`
}
