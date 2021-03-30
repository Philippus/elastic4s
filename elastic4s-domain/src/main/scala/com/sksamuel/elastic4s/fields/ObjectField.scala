package com.sksamuel.elastic4s.fields

case class ObjectField(name: String,
                       dynamic: Option[String] = None,
                       enabled: Option[Boolean] = None,
                       properties: Seq[ElasticField] = Nil) extends ElasticField {
  override def `type`: String = "object"
}
