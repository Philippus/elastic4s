package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.script.ScriptDefinition

case class BucketScriptDefinition(name: String,
                                  script: ScriptDefinition,
                                  bucketsPaths: Map[String,String],
                                  format: Option[String] = None,
                                  gapPolicy: Option[GapPolicy] = None,
                                  metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = BucketScriptDefinition

  def format(format: String): BucketScriptDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): BucketScriptDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): BucketScriptDefinition = copy(metadata = metadata)
}
