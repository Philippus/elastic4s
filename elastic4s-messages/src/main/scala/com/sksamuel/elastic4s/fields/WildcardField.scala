package com.sksamuel.elastic4s.fields

case class WildcardField(override val name: String,
                         ignoreAbove: Option[Int] = None) extends ElasticField {
  override def `type`: String = "wildcard"
}
