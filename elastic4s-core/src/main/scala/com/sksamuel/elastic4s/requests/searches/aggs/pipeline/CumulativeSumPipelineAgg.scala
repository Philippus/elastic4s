package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class CumulativeSumPipelineAgg(name: String,
                                    bucketsPath: String,
                                    format: Option[String] = None,
                                    metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = CumulativeSumPipelineAgg

  def format(format: String): CumulativeSumPipelineAgg                  = copy(format = Some(format))
  def metadata(metadata: Map[String, AnyRef]): CumulativeSumPipelineAgg = copy(metadata = metadata)
}
