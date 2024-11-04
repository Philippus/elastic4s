package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers.common.FetchSourceContextBuilderFn
import com.sksamuel.elastic4s.handlers.searches.HighlightBuilderFn
import com.sksamuel.elastic4s.handlers.searches.queries.sort.SortBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.TopHitsAggregation

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
    agg.fetchSource.foreach(FetchSourceContextBuilderFn(builder, _))

    agg.explain.foreach(builder.field("explain", _))

    if (agg.storedFields.nonEmpty)
      builder.array("stored_fields", agg.storedFields.toArray)

    if (agg.docValueFields.nonEmpty)
      builder.array("docvalue_fields", agg.docValueFields.toArray)

    agg.highlight.foreach { highlight =>
      builder.rawField("highlight", HighlightBuilderFn(highlight))
    }

    agg.version.foreach(builder.field("version", _))

    builder.endObject().endObject()
  }
}
