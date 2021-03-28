package com.sksamuel.elastic4s.fields

case class BinaryField(name: String,
                       docValues: Option[Boolean] = None,
                       store: Option[Boolean] = None) extends ElasticField {
  override def `type`: String = "binary"
}
