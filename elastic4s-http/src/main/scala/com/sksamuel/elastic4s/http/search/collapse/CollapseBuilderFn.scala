package com.sksamuel.elastic4s.http.search.collapse

import com.sksamuel.elastic4s.http.search.queries.nested.InnerHitQueryBodyFn
import com.sksamuel.elastic4s.searches.collapse.CollapseDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object CollapseBuilderFn {

  def apply(collapse: CollapseDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.field("field", collapse.field)
    collapse.maxConcurrentGroupSearches.foreach(max => builder.field("max_concurrent_group_searches", max))
    collapse
      .inner
      .map(InnerHitQueryBodyFn.apply)
      .foreach(x => builder.rawField("inner_hits", new BytesArray(x.string), XContentType.JSON))
    builder.endObject()
    builder
  }
}
