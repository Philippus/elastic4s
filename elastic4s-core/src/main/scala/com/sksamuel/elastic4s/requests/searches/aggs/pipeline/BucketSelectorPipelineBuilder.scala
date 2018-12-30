package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object BucketSelectorPipelineBuilder {
  def apply(agg: BucketSelectorPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("bucket_selector")
    builder.startObject("buckets_path")
    agg.bucketsPathMap.foreach(p => builder.field(p._1, p._2))
    builder.endObject()
    builder.rawField("script", ScriptBuilderFn(agg.script))
    builder.endObject()
  }
}
