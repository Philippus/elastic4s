package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.{IncludeExclude, IncludePartition}
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class SigTermsAggregation(name: String,
                               minDocCount: Option[Long] = None,
                               executionHint: Option[String] = None,
                               size: Option[Int] = None,
                               includeExclude: Option[IncludeExclude] = None,
                               includePartition: Option[IncludePartition] = None,
                               field: Option[String] = None,
                               shardMinDocCount: Option[Long] = None,
                               shardSize: Option[Int] = None,
                               backgroundFilter: Option[Query] = None,
                               subaggs: Seq[AbstractAggregation] = Nil,
                               metadata: Map[String, AnyRef] = Map.empty,
                               heuristic: Option[String] = None,
                               filterDuplicateText: Option[Boolean] = None)
    extends Aggregation {

  type T = SigTermsAggregation

  def minDocCount(min: Long): SigTermsAggregation      = copy(minDocCount = min.some)
  def executionHint(hint: String): SigTermsAggregation = copy(executionHint = hint.some)
  def size(size: Int): SigTermsAggregation             = copy(size = size.some)

  def filterDuplicateText(b: Boolean): T = copy(filterDuplicateText = b.some)

  def includeExclude(include: String, exclude: String): T =
    copy(includeExclude = IncludeExclude(Option(include).toSeq, Option(exclude).toSeq).some)

  def includeExclude(include: Iterable[String], exclude: Iterable[String]): T =
    copy(includeExclude = IncludeExclude(include.toSeq, exclude.toSeq).some)

  def includePartition(partition: Int, numPartitions: Int): T =
    copy(includePartition = IncludePartition(partition, numPartitions).some)

  def significanceHeuristic(heuristic: String): SigTermsAggregation = copy(heuristic = heuristic.some)

  def field(field: String): SigTermsAggregation            = copy(field = field.some)
  def shardMinDocCount(min: Long): SigTermsAggregation     = copy(shardMinDocCount = min.some)
  def backgroundFilter(filter: Query): SigTermsAggregation = copy(backgroundFilter = filter.some)

  def shardSize(shardSize: Int): SigTermsAggregation = copy(shardSize = shardSize.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
