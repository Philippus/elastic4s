package com.sksamuel.elastic4s.http.search.template

import com.sksamuel.elastic4s.searches.TemplateSearchDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object TemplateSearchContentBuilder {
  def apply(req: TemplateSearchDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject()
    builder.field("id", req.name)
    if (req.params.nonEmpty) {
      builder.startObject("params")
      req.params.foreach { case (key, value) => builder.field(key, value) }
      builder.endObject()
    }
    builder.endObject()
  }
}
