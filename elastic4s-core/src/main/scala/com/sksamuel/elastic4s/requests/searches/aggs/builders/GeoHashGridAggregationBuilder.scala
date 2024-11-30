package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{
  AbstractAggregation,
  AggMetaDataFn,
  GeoHashGridAggregation,
  SubAggsBuilderFn
}

object GeoHashGridAggregationBuilder {
  def apply(
      agg: GeoHashGridAggregation,
      customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]
  ): XContentBuilder = {

    val builder = XContentFactory.obj().startObject("geohash_grid")

    agg.field.foreach(builder.field("field", _))
    agg.precision.foreach(builder.field("precision", _))
    agg.size.foreach(builder.field("size", _))
    agg.shardSize.foreach(builder.field("shard_size", _))

    builder.endObject()

    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
