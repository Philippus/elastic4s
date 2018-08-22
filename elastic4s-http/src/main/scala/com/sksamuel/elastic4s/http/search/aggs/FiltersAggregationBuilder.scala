package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.aggs.FiltersAggregationDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object FiltersAggregationBuilder {
  def apply(agg: FiltersAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("filters")
    if (agg.filters.nonEmpty) {
      builder.startArray("filters")
      val filters = agg.filters.map(QueryBuilderFn.apply).map(_.string).mkString(",")
      builder.rawValue(new BytesArray(filters))
      builder.endArray()      
    }
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
