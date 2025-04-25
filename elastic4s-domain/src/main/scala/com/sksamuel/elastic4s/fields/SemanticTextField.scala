package com.sksamuel.elastic4s.fields

object SemanticTextField {
  val `type`: String = "semantic_text"
}
case class SemanticTextField(override val name: String, inferenceId: String, searchInferenceId: Option[String] = None)
    extends ElasticField {
  override def `type`: String = SemanticTextField.`type`
}
