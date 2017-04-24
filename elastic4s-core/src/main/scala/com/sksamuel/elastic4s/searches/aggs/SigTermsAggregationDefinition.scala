package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.aggregations.bucket.significant.heuristics.SignificanceHeuristic
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude

case class SigTermsAggregationDefinition(name: String,
                                         minDocCount: Option[Long] = None,
                                         executionHint: Option[String] = None,
                                         size: Option[Int] = None,
                                         includeExclude: Option[IncludeExclude] = None,
                                         field: Option[String] = None,
                                         shardMinDocCount: Option[Long] = None,
                                         shardSize: Option[Int] = None,
                                         backgroundFilter: Option[QueryDefinition] = None,
                                         subaggs: Seq[AbstractAggregation] = Nil,
                                         metadata: Map[String, AnyRef] = Map.empty,
                                         heuristic: Option[SignificanceHeuristic] = None)
  extends AggregationDefinition {

  type T = SigTermsAggregationDefinition

  def minDocCount(min: Long): SigTermsAggregationDefinition = copy(minDocCount = min.some)
  def executionHint(hint: String): SigTermsAggregationDefinition = copy(executionHint = hint.some)
  def size(size: Int): SigTermsAggregationDefinition = copy(size = size.some)
  def includeExclude(include: String, exclude: String): SigTermsAggregationDefinition =
    copy(includeExclude = new IncludeExclude(include, exclude).some)
  def includeExclude(include: Iterable[String], exclude: Iterable[String]): SigTermsAggregationDefinition = {
    val inc = if (include.isEmpty) null else include.toArray
    val exc = if (exclude.isEmpty) null else exclude.toArray
    copy(includeExclude = new IncludeExclude(inc, exc).some)
  }
  def significanceHeuristic(heuristic: SignificanceHeuristic): SigTermsAggregationDefinition =
    copy(heuristic = heuristic.some)

  def field(field: String): SigTermsAggregationDefinition = copy(field = field.some)
  def shardMinDocCount(min: Long): SigTermsAggregationDefinition = copy(shardMinDocCount = min.some)
  def backgroundFilter(filter: QueryDefinition): SigTermsAggregationDefinition = copy(backgroundFilter = filter.some)

  def shardSize(shardSize: Int): SigTermsAggregationDefinition = copy(shardSize = shardSize.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
