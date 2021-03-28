package com.sksamuel.elastic4s.fields

case class Murmur3Field(name: String) extends ElasticField {
  override def `type`: String = "murmur3"
}
