package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class KeyedFiltersAggregation(name: String,
                                   filters: Iterable[(String, Query)],
                                   otherBucket: Option[Boolean] = None,
                                   otherBucketKey: Option[String] = None,
                                   subaggs: Seq[AbstractAggregation] = Nil,
                                   metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {
  type T = KeyedFiltersAggregation

  def otherBucket(otherBucket: Boolean): T      = copy(otherBucket = otherBucket.some)
  def otherBucketKey(otherBucketKey: String): T = copy(otherBucketKey = otherBucketKey.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
