package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.exts.OptionImplicits._

case class GeoHashGridAggregation(name: String,
                                  field: Option[String] = None,
                                  shardSize: Option[Int] = None,
                                  size: Option[Int] = None,
                                  precision: Option[Int] = None,
                                  subaggs: Seq[AbstractAggregation] = Nil,
                                  metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = GeoHashGridAggregation

  def field(field: String): GeoHashGridAggregation      = copy(field = field.some)
  def precision(precision: Int): GeoHashGridAggregation = copy(precision = precision.some)
  def shardSize(shardSize: Int): GeoHashGridAggregation = copy(shardSize = shardSize.some)
  def size(size: Int): GeoHashGridAggregation           = copy(size = size.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
