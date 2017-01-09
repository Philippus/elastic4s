package com.sksamuel.elastic4s.explain

import com.sksamuel.elastic4s.DocumentRef

class ExplainSyntax {
  def explain(ref: DocumentRef) = ExplainDefinition(ref.index, ref.`type`, ref.id)
  def explain(index: String, `type`: String, id: String) = ExplainDefinition(index, `type`, id)
}
