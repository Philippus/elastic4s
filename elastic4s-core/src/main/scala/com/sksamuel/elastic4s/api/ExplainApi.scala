package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests
import com.sksamuel.elastic4s.requests.common.DocumentRef
import com.sksamuel.elastic4s.requests.explain.ExplainRequest

trait ExplainApi {
  def explain(ref: DocumentRef): ExplainRequest           = requests.explain.ExplainRequest(ref.index, ref.id)
  def explain(index: String, id: String): ExplainRequest  = explain(DocumentRef(index, id = id))
}
