package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class KeyedFiltersAggregationDefinition(name: String,
                                             filters: Iterable[(String, QueryDefinition)],
                                             otherBucket: Option[Boolean] = None,
                                             otherBucketKey: Option[String] = None,
                                             subaggs: Seq[AbstractAggregation] = Nil,
                                             metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {
  type T = KeyedFiltersAggregationDefinition

  def otherBucket(otherBucket: Boolean): T = copy(otherBucket = otherBucket.some)
  def otherBucketKey(otherBucketKey: String): T = copy(otherBucketKey = otherBucketKey.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
