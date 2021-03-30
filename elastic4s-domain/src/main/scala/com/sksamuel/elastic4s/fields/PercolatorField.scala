package com.sksamuel.elastic4s.fields

case class PercolatorField(name: String) extends ElasticField {
  override def `type`: String = "percolator"
}
