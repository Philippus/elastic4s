package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.exts.OptionImplicits._

case class SamplerAggregationDefinition(name: String,
                                        shardSize: Option[Int] = None,
                                        subaggs: Seq[AbstractAggregation] = Nil,
                                        metadata: Map[String, AnyRef] = Map.empty)
    extends AggregationDefinition {

  type T = SamplerAggregationDefinition

  def shardSize(shardSize: Int): T = copy(shardSize = shardSize.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
