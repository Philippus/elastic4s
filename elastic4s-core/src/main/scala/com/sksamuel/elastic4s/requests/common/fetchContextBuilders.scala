package com.sksamuel.elastic4s.requests.common

import com.sksamuel.elastic4s.XContentBuilder

// takes a FetchSourceContext and returns the appropriate json
// https://www.elastic.co/guide/en/elasticsearch/reference/5.6/search-request-source-filtering.html
object FetchSourceContextBuilderFn {
  def apply(builder: XContentBuilder, context: FetchSourceContext): XContentBuilder = {
    if (context.fetchSource)
      if (context.includes.nonEmpty || context.excludes.nonEmpty) {
        builder.startObject("_source")
        context.includes.toList match {
          case Nil      =>
          case includes => builder.array("includes", includes.toArray)
        }
        context.excludes.toList match {
          case Nil      =>
          case excludes => builder.array("excludes", excludes.toArray)
        }
        builder.endObject()
      } else
        builder.field("_source", true)
    else
      builder.field("_source", false)
    builder
  }
}

object FetchSourceContextQueryParameterFn {
  def apply(context: FetchSourceContext): Map[String, String] = {
    val map = scala.collection.mutable.Map.empty[String, String]
    if (context.fetchSource) {
      map.put("_source", "true")
      if (context.includes.nonEmpty)
        map.put("_source_includes", context.includes.mkString(","))
      if (context.excludes.nonEmpty)
        map.put("_source_excludes", context.excludes.mkString(","))
    } else
      map.put("_source", "false")
    map.toMap
  }
}
