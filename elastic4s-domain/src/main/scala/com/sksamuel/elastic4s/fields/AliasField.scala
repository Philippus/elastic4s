package com.sksamuel.elastic4s.fields

object AliasField {
  val `type` = "alias"
}
case class AliasField(name: String, path: String) extends ElasticField {
  override def `type`: String = AliasField.`type`
}
