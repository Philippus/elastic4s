package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.elastic4s.requests.searches.aggs.AggMetaDataFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object MovFnPipelineAggBuilder {
  def apply(agg: MovFnPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("moving_fn")
    builder.field("buckets_path", agg.bucketsPath)
    builder.field("window", agg.window)
    builder.field("script", agg.script)
    agg.gapPolicy.foreach(policy => builder.field("gap_policy", policy.toString.toLowerCase))
    agg.shift.foreach(p => builder.field("shift", p))
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
