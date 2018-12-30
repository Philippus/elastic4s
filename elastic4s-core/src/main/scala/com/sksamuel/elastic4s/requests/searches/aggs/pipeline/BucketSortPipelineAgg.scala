package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import com.sksamuel.elastic4s.requests.searches.sort.Sort

case class BucketSortPipelineAgg(name: String,
                                 sort: Seq[Sort],
                                 from: Option[Int] = None,
                                 size: Option[Int] = None,
                                 gapPolicy: Option[GapPolicy] = None,
                                 metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = BucketSortPipelineAgg

  def from(from: Int): BucketSortPipelineAgg = copy(from = Some(from))

  def gapPolicy(gapPolicy: GapPolicy): BucketSortPipelineAgg = copy(gapPolicy = Some(gapPolicy))

  def metadata(metadata: Map[String, AnyRef]): BucketSortPipelineAgg = copy(metadata = metadata)

  def size(size: Int): BucketSortPipelineAgg = copy(size = Some(size))

  def sort(first: Sort, rest: Sort*): BucketSortPipelineAgg = sort(first +: rest)
  def sort(sorts: Iterable[Sort]): BucketSortPipelineAgg    = copy(sort = sorts.toSeq)
}
