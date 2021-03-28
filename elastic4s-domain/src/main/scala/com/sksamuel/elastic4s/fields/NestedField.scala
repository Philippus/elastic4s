package com.sksamuel.elastic4s.fields

case class NestedField(name: String,
                       dynamic: Option[String] = None,
                       enabled: Option[Boolean] = None,
                       properties: Seq[ElasticField] = Nil,
                       includeInParent: Option[Boolean] = None,
                       includeInRoot: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "nested"
}
