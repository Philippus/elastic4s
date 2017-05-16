package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.script.ScriptDefinition

case class BucketSelectorDefinition(name: String,
                                    script: ScriptDefinition,
                                    bucketsPaths: Seq[String],
                                    gapPolicy: Option[GapPolicy] = None,
                                    metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = BucketSelectorDefinition

  def gapPolicy(gapPolicy: GapPolicy): BucketSelectorDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): BucketSelectorDefinition = copy(metadata = metadata)
}
