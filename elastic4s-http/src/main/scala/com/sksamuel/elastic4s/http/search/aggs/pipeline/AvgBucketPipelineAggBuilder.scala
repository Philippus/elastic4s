package com.sksamuel.elastic4s.http.search.aggs.pipeline

import com.sksamuel.elastic4s.http.search.aggs.AggMetaDataFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.pipeline.AvgBucketPipelineAgg

object AvgBucketPipelineAggBuilder {
  def apply(agg: AvgBucketPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("avg_bucket")
    builder.field("buckets_path", agg.bucketsPath)
    agg.gapPolicy.foreach(policy => builder.field("gap_policy", policy.toString.toLowerCase))
    agg.format.foreach(f => builder.field("format", f))
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
