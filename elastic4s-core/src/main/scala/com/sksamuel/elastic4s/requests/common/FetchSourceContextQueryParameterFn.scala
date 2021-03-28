package com.sksamuel.elastic4s.requests.common

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
