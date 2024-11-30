package com.sksamuel.elastic4s.handlers.pit

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.pit.DeletePitRequest

object DeletePitBuilderFn {
  def apply(request: DeletePitRequest): XContentBuilder =
    XContentFactory.jsonBuilder().field("id", request.id)
}
