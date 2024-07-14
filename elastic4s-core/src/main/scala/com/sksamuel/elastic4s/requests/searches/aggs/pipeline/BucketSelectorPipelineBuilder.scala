package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.elastic4s.handlers.script
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object BucketSelectorPipelineBuilder {
  def apply(agg: BucketSelectorPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("bucket_selector")
    builder.startObject("buckets_path")
    agg.bucketsPathMap.foreach(p => builder.field(p._1, p._2))
    builder.endObject()
    builder.rawField("script", script.ScriptBuilderFn(agg.script))
    builder.endObject()
  }
}
