package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.exts.OptionImplicits._

case class SamplerAggregation(name: String,
                              shardSize: Option[Int] = None,
                              subaggs: Seq[AbstractAggregation] = Nil,
                              metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = SamplerAggregation

  def shardSize(shardSize: Int): T = copy(shardSize = shardSize.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
