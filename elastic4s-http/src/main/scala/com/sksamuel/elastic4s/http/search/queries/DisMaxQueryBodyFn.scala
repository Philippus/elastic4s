package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.DisMaxQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object DisMaxQueryBodyFn {
  def apply(q: DisMaxQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("dis_max")
    q.tieBreaker.foreach(builder.field("tie_breaker", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.startArray()
    for (query <- q.queries) {
      builder.rawValue(QueryBuilderFn(query).bytes())
    }
    builder.endArray()
    builder.endObject()
    builder.endObject()
  }
}
