package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.SemanticTextField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object SemanticTextFieldBuilderFn {
  def toField(name: String, values: Map[String, Any]): SemanticTextField = SemanticTextField(
    name,
    values.get("inference_id").map(_.asInstanceOf[String]).get,
    values.get("search_inference_id").map(_.asInstanceOf[String])
  )

  def build(field: SemanticTextField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    builder.field("inference_id", field.inferenceId)
    field.searchInferenceId.foreach(builder.field("search_inference_id", _))
    builder.endObject()
  }
}
