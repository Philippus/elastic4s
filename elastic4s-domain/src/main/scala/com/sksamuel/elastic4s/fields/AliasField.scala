package com.sksamuel.elastic4s.fields

case class AliasField(name: String,
                      path: String) extends ElasticField {
  override def `type`: String = "alias"
}
