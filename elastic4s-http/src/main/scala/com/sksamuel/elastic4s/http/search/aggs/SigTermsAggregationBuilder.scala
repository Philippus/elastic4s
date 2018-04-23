package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.aggs.SigTermsAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object SigTermsAggregationBuilder {
  def apply(agg: SigTermsAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()

    builder.startObject("significant_terms")
    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.executionHint.foreach(builder.field("execution_hint", _))
    agg.size.foreach(builder.field("size", _))
    agg.includeExclude.foreach(_.toXContent(builder, null))
    agg.field.foreach(builder.field("field", _))
    agg.shardMinDocCount.foreach(builder.field("shard_min_doc_count", _))
    agg.shardSize.foreach(builder.field("shard_size", _))
    agg.backgroundFilter.map(QueryBuilderFn.apply).foreach { x =>
      builder.rawField("background_filter", x.bytes(), XContentType.JSON)
    }
    agg.heuristic.foreach(_.toXContent(builder, null))
    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
