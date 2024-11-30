package com.sksamuel.elastic4s.fields

object BinaryField       {
  val `type`: String = "binary"
}
case class BinaryField(name: String, docValues: Option[Boolean] = None, store: Option[Boolean] = None)
    extends ElasticField {
  override def `type`: String = BinaryField.`type`
}
