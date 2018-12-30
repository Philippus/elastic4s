package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.exts.OptionImplicits._

case class DiffPipelineAgg(name: String,
                           bucketsPath: String,
                           format: Option[String] = None,
                           gapPolicy: Option[GapPolicy] = None,
                           lag: Option[Int] = None,
                           metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = DiffPipelineAgg

  def format(format: String): DiffPipelineAgg                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): DiffPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def lag(lag: Int): DiffPipelineAgg                           = copy(lag = lag.some)
  def metadata(metadata: Map[String, AnyRef]): DiffPipelineAgg = copy(metadata = metadata)
}
