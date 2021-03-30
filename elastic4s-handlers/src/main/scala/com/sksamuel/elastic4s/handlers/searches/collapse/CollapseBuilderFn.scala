package com.sksamuel.elastic4s.handlers.searches.collapse

import com.sksamuel.elastic4s.handlers.searches.queries.nested.InnerHitQueryBodyBuilder
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.collapse.CollapseRequest

object CollapseBuilderFn {

  def apply(collapse: CollapseRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("field", collapse.field)
    collapse.maxConcurrentGroupSearches.foreach(max => builder.field("max_concurrent_group_searches", max))
    collapse.inner
      .map(InnerHitQueryBodyBuilder.toJson)
      .foreach(x => builder.field("inner_hits", x))
    builder.endObject()
    builder
  }
}
