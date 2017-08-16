package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.SortBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.TopHitsAggregationDefinition

object TopHitsAggregationBuilder {

  def apply(agg: TopHitsAggregationDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("top_hits")

    agg.size.foreach(builder.field("size", _))
    if (agg.sorts.nonEmpty) {
      builder.startArray("sort")
      agg.sorts.foreach { sort =>
        builder.rawValue(SortBuilderFn(sort))
      }
      builder.endArray()
    }

    // source filtering
    agg.fetchSource foreach { context =>
      if (context.fetchSource) {
        if (context.includes.nonEmpty || context.excludes.nonEmpty) {
          builder.startObject("_source")
          builder.array("includes", context.includes)
          builder.array("excludes", context.excludes)
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
