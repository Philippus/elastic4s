package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.searches.sort.Sort

case class BucketSortDefinition(name: String,
                                sort: Seq[Sort],
                                from: Option[Int] = None,
                                size: Option[Int] = None,
                                gapPolicy: Option[GapPolicy] = None,
                                metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAggregationDefinition {

  type T = BucketSortDefinition

  def from(from: Int): BucketSortDefinition = copy(from = Some(from))

  def gapPolicy(gapPolicy: GapPolicy): BucketSortDefinition = copy(gapPolicy = Some(gapPolicy))

  def metadata(metadata: Map[String, AnyRef]): BucketSortDefinition = copy(metadata = metadata)

  def size(size: Int): BucketSortDefinition = copy(size = Some(size))

  def sort(first: Sort, rest: Sort*): BucketSortDefinition = sort(first +: rest)
  def sort(sorts: Iterable[Sort]): BucketSortDefinition    = copy(sort = sorts.toSeq)
}
