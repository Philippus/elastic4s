package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.requests.exists.ExistsRequest

trait ExistsApi {
  @deprecated("create instance of ExistsRequest directly, eg ExistsRequest(123, Index(\"foo\"))")
  def exists(id: String, index: Index): ExistsRequest = ExistsRequest(id, index)
}
