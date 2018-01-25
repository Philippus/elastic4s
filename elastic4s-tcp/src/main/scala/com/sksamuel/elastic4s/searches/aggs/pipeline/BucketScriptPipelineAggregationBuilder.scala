package com.sksamuel.elastic4s.searches.aggs.pipeline

import java.util

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketscript.BucketScriptPipelineAggregationBuilder

import scala.collection.JavaConverters._

object BucketScriptPipelineAggregationBuilder {

  def apply(p: BucketScriptDefinition): BucketScriptPipelineAggregationBuilder = {
    val jmap: util.HashMap[String, String] = new java.util.HashMap[String, String]()

    p.bucketsPaths.zipWithIndex.foreach { case(x, i) =>
      jmap.put(s"_value$i", x)
    }

    val builder = PipelineAggregatorBuilders.bucketScript(p.name, jmap, ScriptBuilder(p.script))
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.format.foreach(builder.format)
    p.gapPolicy.foreach(builder.gapPolicy)
    builder
  }
}
