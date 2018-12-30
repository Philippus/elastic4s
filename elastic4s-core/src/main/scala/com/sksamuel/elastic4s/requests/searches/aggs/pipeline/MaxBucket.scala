package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class MaxBucket(name: String,
                     bucketsPath: String,
                     format: Option[String] = None,
                     gapPolicy: Option[GapPolicy] = None,
                     metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = MaxBucket

  def format(format: String): MaxBucket                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MaxBucket         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MaxBucket = copy(metadata = metadata)
}
