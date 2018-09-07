package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.aggs.pipeline.BucketScriptDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object BucketScriptBuilder {
  def apply(agg: BucketScriptDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("bucket_script")
    if (agg.bucketsPaths.nonEmpty) {
      builder.startObject("buckets_path")
      agg.bucketsPaths.zipWithIndex.foreach { case(x, i) =>
        builder.field(s"_value$i", x)
      }
      builder.endObject()
    }
    builder.rawField("script", ScriptBuilderFn(agg.script).bytes, XContentType.JSON)
    agg.format.foreach(builder.field("format", _))
    agg.gapPolicy.foreach(builder.field("gapPolicy", _))
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
