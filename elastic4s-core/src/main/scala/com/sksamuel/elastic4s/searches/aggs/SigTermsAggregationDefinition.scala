package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.{IncludeExclude, IncludePartition}
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class SigTermsAggregationDefinition(name: String,
                                         minDocCount: Option[Long] = None,
                                         executionHint: Option[String] = None,
                                         size: Option[Int] = None,
                                         includeExclude: Option[IncludeExclude] = None,
                                         includePartition: Option[IncludePartition] = None,
                                         field: Option[String] = None,
                                         shardMinDocCount: Option[Long] = None,
                                         shardSize: Option[Int] = None,
                                         backgroundFilter: Option[QueryDefinition] = None,
                                         subaggs: Seq[AbstractAggregation] = Nil,
                                         metadata: Map[String, AnyRef] = Map.empty,
                                         heuristic: Option[String] = None)
  extends AggregationDefinition {

  type T = SigTermsAggregationDefinition

  def minDocCount(min: Long): SigTermsAggregationDefinition = copy(minDocCount = min.some)
  def executionHint(hint: String): SigTermsAggregationDefinition = copy(executionHint = hint.some)
  def size(size: Int): SigTermsAggregationDefinition = copy(size = size.some)

  def includeExclude(include: String, exclude: String): T =
    copy(includeExclude = IncludeExclude(Option(include).toSeq, Option(exclude).toSeq).some)

  def includeExclude(include: Iterable[String], exclude: Iterable[String]): T = {
    copy(includeExclude = IncludeExclude(include.toSeq, exclude.toSeq).some)
  }

  def includePartition(partition: Int, numPartitions: Int): T = {
    copy(includePartition = IncludePartition(partition, numPartitions).some)
  }

  def significanceHeuristic(heuristic: String): SigTermsAggregationDefinition = copy(heuristic = heuristic.some)

  def field(field: String): SigTermsAggregationDefinition = copy(field = field.some)
  def shardMinDocCount(min: Long): SigTermsAggregationDefinition = copy(shardMinDocCount = min.some)
  def backgroundFilter(filter: QueryDefinition): SigTermsAggregationDefinition = copy(backgroundFilter = filter.some)

  def shardSize(shardSize: Int): SigTermsAggregationDefinition = copy(shardSize = shardSize.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)
}
