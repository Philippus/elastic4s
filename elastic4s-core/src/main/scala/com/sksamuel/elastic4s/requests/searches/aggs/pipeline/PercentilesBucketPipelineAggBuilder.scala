package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.elastic4s.requests.searches.aggs.AggMetaDataFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object PercentilesBucketPipelineAggBuilder {
  def apply(agg: PercentilesBucketPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("percentiles_bucket")
    builder.field("buckets_path", agg.bucketsPath)
    agg.gapPolicy.foreach(policy => builder.field("gap_policy", policy.toString.toLowerCase))
    agg.format.foreach(f => builder.field("format", f))
    if (agg.percents.nonEmpty)
      builder.array("percents", agg.percents.toArray)
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
