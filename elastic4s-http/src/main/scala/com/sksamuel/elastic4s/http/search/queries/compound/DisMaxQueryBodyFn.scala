package com.sksamuel.elastic4s.http.search.queries.compound

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.DisMaxQueryDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object DisMaxQueryBodyFn {
  def apply(q: DisMaxQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("dis_max")
    q.tieBreaker.foreach(builder.field("tie_breaker", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.startArray()
    // Workaround for bug where separator is not added with rawValues
    val arrayBody = new BytesArray(q.queries.map(q => QueryBuilderFn(q).string()).mkString(","))
    builder.rawValue(arrayBody, XContentType.JSON)
    builder.endArray()

    builder.endObject()
    builder.endObject()
  }
}
