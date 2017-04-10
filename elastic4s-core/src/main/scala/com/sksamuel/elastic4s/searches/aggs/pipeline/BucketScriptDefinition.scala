package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy

case class BucketScriptDefinition(name: String,
                                  script: ScriptDefinition,
                                  bucketsPaths: Seq[String],
                                  format: Option[String] = None,
                                  gapPolicy: Option[GapPolicy] = None,
                                  metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = BucketScriptDefinition

  def builder: T = ???
//    val builder = PipelineAggregatorBuilders.bucketScript(name, ScriptBuilder(script), bucketsPaths: _*)
//    format.foreach(builder.format)
//    gapPolicy.foreach(builder.gapPolicy)
//    builder
//  }

  def format(format: String): BucketScriptDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): BucketScriptDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): BucketScriptDefinition = copy(metadata = metadata)
}
