package com.sksamuel.elastic4s.requests.searches.template

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.TemplateSearchRequest

object TemplateSearchBuilderFn {
  def apply(req: TemplateSearchRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("id", req.name)
    if (req.params.nonEmpty) {
      builder.startObject("params")
      req.params.foreach { case (key, value) => builder.autofield(key, value) }
      builder.endObject()
    }
    builder.endObject()
  }
}
