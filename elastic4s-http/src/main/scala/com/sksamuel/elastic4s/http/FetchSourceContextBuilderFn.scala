package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.FetchSourceContext
import com.sksamuel.elastic4s.json.XContentBuilder

// takes a FetchSourceContext and returns the appropriate json
// https://www.elastic.co/guide/en/elasticsearch/reference/5.6/search-request-source-filtering.html
object FetchSourceContextBuilderFn {
  def apply(builder: XContentBuilder, context: FetchSourceContext): XContentBuilder = {
    if (context.fetchSource) {
      if (context.includes.nonEmpty || context.excludes.nonEmpty) {
        builder.startObject("_source")
        builder.array("includes", context.includes)
        builder.array("excludes", context.excludes)
        builder.endObject()
      } else {
        builder.field("_source", true)
      }
    } else {
      builder.field("_source", false)
    }
    builder
  }
}

object FetchSourceContextQueryParameterFn {
  def apply(context: FetchSourceContext): Map[String, String] = {
    val map = scala.collection.mutable.Map.empty[String, String]
    if (context.fetchSource) {
      map.put("_source", "true")
      if (context.includes.nonEmpty) {
        map.put("_source_include", context.includes.mkString(","))
      }
      if (context.excludes.nonEmpty) {
        map.put("_source_exclude", context.excludes.mkString(","))
      }
    } else {
      map.put("_source", "false")
    }
    map.toMap
  }
}
