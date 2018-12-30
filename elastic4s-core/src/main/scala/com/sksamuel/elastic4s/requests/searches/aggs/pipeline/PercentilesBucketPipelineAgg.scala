package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class PercentilesBucketPipelineAgg(name: String,
                                        bucketsPath: String,
                                        format: Option[String] = None,
                                        gapPolicy: Option[GapPolicy] = None,
                                        percents: Seq[Double] = Nil,
                                        metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = PercentilesBucketPipelineAgg

  def format(format: String): PercentilesBucketPipelineAgg                  = copy(format = Some(format))
  def percents(first: Double, rest: Double*): PercentilesBucketPipelineAgg  = percents(first +: rest)
  def percents(percents: Seq[Double]): PercentilesBucketPipelineAgg         = copy(percents = percents)
  def gapPolicy(gapPolicy: GapPolicy): PercentilesBucketPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): PercentilesBucketPipelineAgg = copy(metadata = metadata)
}
