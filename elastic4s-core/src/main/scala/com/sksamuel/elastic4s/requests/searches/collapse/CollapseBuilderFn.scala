package com.sksamuel.elastic4s.requests.searches.collapse

import com.sksamuel.elastic4s.requests.searches.queries.nested.InnerHitQueryBodyFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object CollapseBuilderFn {

  def apply(collapse: CollapseRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("field", collapse.field)
    collapse.maxConcurrentGroupSearches.foreach(max => builder.field("max_concurrent_group_searches", max))
    collapse.inner
      .map(InnerHitQueryBodyFn.apply)
      .foreach(x => builder.rawField("inner_hits", x.string))
    builder.endObject()
    builder
  }
}
