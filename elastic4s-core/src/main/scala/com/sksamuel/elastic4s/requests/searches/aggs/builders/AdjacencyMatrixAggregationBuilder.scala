package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, AdjacencyMatrixAggregation, AggMetaDataFn, SubAggsBuilderFn}

object AdjacencyMatrixAggregationBuilder {
  def apply(agg: AdjacencyMatrixAggregation, customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("adjacency_matrix")
    agg.separator.foreach(builder.field("separator", _))
    builder.startObject("filters")
    agg.filters.foreach { case (name, query) =>
      builder.rawField(name, QueryBuilderFn(query))
    }
    builder.endObject()
    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}

