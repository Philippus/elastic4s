package com.sksamuel.elastic4s.fields

object AnnotatedTextField {
  val `type` = "annotated_text"
}
case class AnnotatedTextField(name: String, copyTo: Seq[String] = Nil) extends ElasticField {
  override def `type`: String = AnnotatedTextField.`type`
}
