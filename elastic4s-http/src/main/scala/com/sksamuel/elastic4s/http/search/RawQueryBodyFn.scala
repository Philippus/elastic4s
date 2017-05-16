package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.RawQueryDefinition

object RawQueryBodyFn {
  def apply(q: RawQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    val parser = XContentFactory.xContent(XContentType.JSON).createParser(NamedXContentRegistry.EMPTY, q.json)
    try {
      builder.copyCurrentStructure(parser)
    } finally {
      parser.close()
    }
  }
}
