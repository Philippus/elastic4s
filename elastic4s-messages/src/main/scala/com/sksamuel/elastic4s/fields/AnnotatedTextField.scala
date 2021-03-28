package com.sksamuel.elastic4s.fields

case class AnnotatedTextField(name: String) extends ElasticField {
  override def `type`: String = "annotated_text"
}
