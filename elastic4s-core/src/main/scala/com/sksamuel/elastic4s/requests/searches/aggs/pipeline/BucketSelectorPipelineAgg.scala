package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.elastic4s.requests.script.Script

case class BucketSelectorPipelineAgg(name: String,
                                     script: Script,
                                     bucketsPathMap: Map[String, String],
                                     gapPolicy: Option[GapPolicy] = None,
                                     metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = BucketSelectorPipelineAgg

  def gapPolicy(gapPolicy: GapPolicy): BucketSelectorPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): BucketSelectorPipelineAgg = copy(metadata = metadata)
}
