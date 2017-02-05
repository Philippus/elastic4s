package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.http.search.queries.{QueryBuilderFn, SortContentBuilder}
import com.sksamuel.elastic4s.searches.SearchDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import scala.collection.JavaConverters._

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

    // source filtering
    request.fetchContext foreach { context =>
      if (context.fetchSource) {
        if (context.includes.nonEmpty || context.excludes.nonEmpty) {
          builder.startObject("_source")
          builder.field("includes", context.includes.toList.asJava)
          builder.field("excludes", context.excludes.toList.asJava)
          builder.endObject()
        }
      } else {
        builder.field("_source", false)
      }
    }

    builder.endObject()
    builder
  }
}
