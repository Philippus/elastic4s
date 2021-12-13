package com.sksamuel.elastic4s.fields

object ConstantKeywordField {
  val `type`: String = "constant_keyword"
}
case class ConstantKeywordField(override val name: String, value: String) extends ElasticField {
  override def `type`: String = ConstantKeywordField.`type`
}
