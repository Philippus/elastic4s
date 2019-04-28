package com.sksamuel.elastic4s.requests.explain

import com.sksamuel.elastic4s.requests.common.DocumentRef

trait ExplainApi {
  def explain(ref: DocumentRef): ExplainRequest           = ExplainRequest(ref.index, ref.id)
  def explain(index: String, id: String): ExplainRequest  = explain(DocumentRef(index, id = id))
}
