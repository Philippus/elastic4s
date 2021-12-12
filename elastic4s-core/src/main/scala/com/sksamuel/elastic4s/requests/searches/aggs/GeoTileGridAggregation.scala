package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class GeoTileGridAggregation(
  name: String,
  field: Option[String] = None,
  shardSize: Option[Int] = None,
  size: Option[Int] = None,
  precision: Option[Int] = None,
  subaggs: Seq[AbstractAggregation] = Nil,
  metadata: Map[String, AnyRef] = Map.empty)
  extends Aggregation {

  type T = GeoTileGridAggregation

  def field(field: String): GeoTileGridAggregation      = copy(field = field.some)
  def precision(precision: Int): GeoTileGridAggregation = copy(precision = precision.some)
  def shardSize(shardSize: Int): GeoTileGridAggregation = copy(shardSize = shardSize.some)
  def size(size: Int): GeoTileGridAggregation           = copy(size = size.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
