package com.sksamuel.elastic4s.explain

import com.sksamuel.elastic4s.DocumentRef

trait ExplainApi {
  def explain(ref: DocumentRef): ExplainDefinition                          = ExplainDefinition(ref.indexAndType, ref.id)
  def explain(index: String, `type`: String, id: String): ExplainDefinition = explain(DocumentRef(index, `type`, id))
}
