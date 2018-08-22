package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.aggs.KeyedFiltersAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object KeyedFiltersAggregationBuilder {
  def apply(agg: KeyedFiltersAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("filters")
    (agg.otherBucketKey, agg.otherBucket) match {
      case (Some(key), _) =>
        builder.field("other_bucket", true)
        builder.field("other_bucket_key", key)
      case (None, Some(bool)) =>
        builder.field("other_bucket", bool)
      case _ =>
    }
    builder.startObject("filters")
    agg.filters.foreach { case (name, query) => 
      builder.rawField(name, QueryBuilderFn(query).bytes, XContentType.JSON)
    }
    builder.endObject()
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
