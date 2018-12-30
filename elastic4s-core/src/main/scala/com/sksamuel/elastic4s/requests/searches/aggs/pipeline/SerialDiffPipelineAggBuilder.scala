package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.elastic4s.requests.searches.aggs.AggMetaDataFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object SerialDiffPipelineAggBuilder {
  def apply(agg: DiffPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("serial_diff")
    builder.field("buckets_path", agg.bucketsPath)
    agg.gapPolicy.foreach(policy => builder.field("gap_policy", policy.toString.toLowerCase))
    agg.format.foreach(f => builder.field("format", f))
    agg.lag.foreach(l => builder.field("lag", l))
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
