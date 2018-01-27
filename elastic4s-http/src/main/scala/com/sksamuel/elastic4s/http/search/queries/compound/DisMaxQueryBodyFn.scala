package com.sksamuel.elastic4s.http.search.queries.compound

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.DisMaxQueryDefinition

object DisMaxQueryBodyFn {
  def apply(q: DisMaxQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("dis_max")
    q.tieBreaker.foreach(builder.field("tie_breaker", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.startArray("queries")
    // Workaround for bug where separator is not added with rawValues
    q.queries.map(QueryBuilderFn.apply).foreach { query =>
      builder.rawValue(query)
    }
    builder.endArray()

    builder.endObject()
    builder.endObject()
  }
}
