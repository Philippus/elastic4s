package com.sksamuel.elastic4s.fields

object Murmur3Field {
  val `type`: String = "murmur3"
}
case class Murmur3Field(name: String) extends ElasticField {
  override def `type`: String = Murmur3Field.`type`
}
