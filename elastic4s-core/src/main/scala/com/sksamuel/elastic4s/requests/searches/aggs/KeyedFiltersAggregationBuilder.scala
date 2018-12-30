package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object KeyedFiltersAggregationBuilder {
  def apply(agg: KeyedFiltersAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("filters")
    agg.otherBucket.foreach(builder.field("other_bucket", _))
    agg.otherBucketKey.foreach(builder.field("other_bucket_key", _))

    builder.startObject("filters")
    agg.filters.map(map => builder.rawField(map._1, QueryBuilderFn(map._2)))
    builder.endObject()

    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder
  }
}
