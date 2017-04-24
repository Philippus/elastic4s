package com.sksamuel.elastic4s.searches.aggs.pipeline

case class CumulativeSumDefinition(name: String,
                                   bucketsPath: String,
                                   format: Option[String] = None,
                                   metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = CumulativeSumDefinition

  def format(format: String): CumulativeSumDefinition = copy(format = Some(format))
  def metadata(metadata: Map[String, AnyRef]): CumulativeSumDefinition = copy(metadata = metadata)
}
