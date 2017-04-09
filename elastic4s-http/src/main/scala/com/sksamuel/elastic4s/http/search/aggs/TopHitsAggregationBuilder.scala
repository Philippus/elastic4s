package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.SortContentBuilder
import com.sksamuel.elastic4s.searches.aggs.TopHitsAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

import scala.collection.JavaConverters._

object TopHitsAggregationBuilder {

  def apply(agg: TopHitsAggregationDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject().startObject("top_hits")

    agg.size.foreach(builder.field("size", _))
    if (agg.sorts.nonEmpty) {
      builder.startArray("sort")
      agg.sorts.foreach { sort =>
        builder.rawValue(SortContentBuilder(sort).bytes, XContentType.JSON)
      }
      builder.endArray()
    }

    // source filtering
    agg.fetchSource foreach { context =>
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

    agg.explain.foreach(builder.field("explain", _))
    agg.version.foreach(builder.field("version", _))

    builder.endObject().endObject()
  }
}
