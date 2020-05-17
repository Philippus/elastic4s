package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.sort.Sort
import com.sksamuel.exts.OptionImplicits._

case class Metric()

case class TopMetricsAggregation(name: String,
                                 metrics: List[String] = Nil,
                                 size: Option[Int] = None,
                                 sort: Option[Sort] = None,
                                 subaggs: Seq[AbstractAggregation] = Nil,
                                 metadata: Map[String, AnyRef] = Map.empty) extends Aggregation {

  type T = TopMetricsAggregation

  def withMetrics(field: String, fields: String*): TopMetricsAggregation = copy(metrics = (field +: fields).toList)
  def withMetrics(fields: Iterable[String]): TopMetricsAggregation = copy(metrics = fields.toList)

  def withSize(size: Int): TopMetricsAggregation = copy(size = size.some)
  def withSort(sort: Sort): TopMetricsAggregation = copy(sort = sort.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)
}
