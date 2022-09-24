package com.sksamuel.elastic4s.fields

object VersionField {
  val `type`: String = "version"
}
case class VersionField(name: String) extends ElasticField {
  override def `type`: String = VersionField.`type`
}
