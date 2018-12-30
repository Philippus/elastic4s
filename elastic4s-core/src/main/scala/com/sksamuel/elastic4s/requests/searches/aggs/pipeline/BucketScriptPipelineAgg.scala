package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.elastic4s.requests.script.Script

case class BucketScriptPipelineAgg(name: String,
                                   script: Script,
                                   bucketsPaths: Map[String, String],
                                   format: Option[String] = None,
                                   gapPolicy: Option[GapPolicy] = None,
                                   metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = BucketScriptPipelineAgg

  def format(format: String): BucketScriptPipelineAgg                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): BucketScriptPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): BucketScriptPipelineAgg = copy(metadata = metadata)
}
