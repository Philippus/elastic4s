package com.sksamuel.elastic4s.handlers.common

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.common.FetchSourceContext

// takes a FetchSourceContext and returns the appropriate json
// https://www.elastic.co/guide/en/elasticsearch/reference/5.6/search-request-source-filtering.html
object FetchSourceContextBuilderFn {
  def apply(builder: XContentBuilder, context: FetchSourceContext) {
    if (context.fetchSource)
      if (context.includes.nonEmpty || context.excludes.nonEmpty) {
        builder.startObject("_source")
        context.includes.toList match {
          case Nil =>
          case includes => builder.array("includes", includes.toArray)
        }
        context.excludes.toList match {
          case Nil =>
          case excludes => builder.array("excludes", excludes.toArray)
        }
        builder.endObject()
      } else
        builder.field("_source", true)
    else
      builder.field("_source", false)
    builder.value
  }
}
