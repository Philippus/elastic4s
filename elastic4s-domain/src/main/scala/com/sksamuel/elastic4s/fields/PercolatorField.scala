package com.sksamuel.elastic4s.fields

object PercolatorField {
  val `type`: String = "percolator"
}
case class PercolatorField(name: String) extends ElasticField {
  override def `type`: String = PercolatorField.`type`
}
