package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.SortContentBuilder
import com.sksamuel.elastic4s.searches.aggs.TopHitsAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object TopHitsAggregationBuilder {
  def apply(agg: TopHitsAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("top_hits")
    agg.size.foreach(builder.field("size", _))
    if (agg.sorts.nonEmpty) {
      builder.startArray("sort")
      agg.sorts.foreach { sort =>
        builder.rawValue(SortContentBuilder(sort).bytes())
      }
      builder.endArray()
    }
    agg.explain.foreach(builder.field("explain", _))
    agg.version.foreach(builder.field("version", _))
    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    builder.endObject()
  }
}
