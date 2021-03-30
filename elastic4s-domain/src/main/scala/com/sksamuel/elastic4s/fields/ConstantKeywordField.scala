package com.sksamuel.elastic4s.fields

case class ConstantKeywordField(override val name: String, value: String) extends ElasticField {
  override def `type`: String = "constant_keyword"
}
