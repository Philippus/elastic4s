package com.sksamuel.elastic4s.fields

case class VersionField(name: String) extends ElasticField {
  override def `type`: String = "version"
}
