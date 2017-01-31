package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.http.search.queries.{QueryBuilderFn, SortContentBuilder}
import com.sksamuel.elastic4s.searches.SearchDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object SearchContentBuilder {
  def apply(request: SearchDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    request.query.map(QueryBuilderFn.apply).foreach(x => builder.rawField("query", new BytesArray(x.string)))
    if (request.sorts.nonEmpty) {
      builder.startArray("sort")
      request.sorts.foreach { sort =>
        builder.rawValue(new BytesArray(SortContentBuilder(sort).string))
      }
      builder.endArray()
    }
    builder.endObject()
    builder
  }
}
