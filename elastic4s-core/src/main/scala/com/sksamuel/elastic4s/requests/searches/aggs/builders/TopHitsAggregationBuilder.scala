package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers.common.FetchSourceContextBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.TopHitsAggregation
import com.sksamuel.elastic4s.requests.searches.queries.SortBuilderFn

object TopHitsAggregationBuilder {

  def apply(agg: TopHitsAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("top_hits")

    agg.size.foreach(builder.field("size", _))
    agg.from.foreach(builder.field("from", _))
    if (agg.sorts.nonEmpty) {
      builder.startArray("sort")
      agg.sorts.foreach { sort =>
        builder.rawValue(SortBuilderFn(sort))
      }
      builder.endArray()
    }

    // source filtering
    agg.fetchSource.foreach { it => builder.value(FetchSourceContextBuilderFn.toJson(it)) }

    agg.explain.foreach(builder.field("explain", _))
    agg.version.foreach(builder.field("version", _))

    builder.endObject().endObject()
  }
}
