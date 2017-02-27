package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.exts.OptionImplicits._

case class GeoHashGridAggregationDefinition(name: String,
                                            field: Option[String] = None,
                                            shardSize: Option[Int] = None,
                                            size: Option[Int] = None,
                                            precision: Option[Int] = None,
                                            pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                            subaggs: Seq[AggregationDefinition] = Nil,
                                            metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = GeoHashGridAggregationDefinition

  def field(field: String): GeoHashGridAggregationDefinition = copy(field = field.some)
  def precision(precision: Int): GeoHashGridAggregationDefinition = copy(precision = precision.some)
  def shardSize(shardSize: Int): GeoHashGridAggregationDefinition = copy(shardSize = shardSize.some)
  def size(size: Int): GeoHashGridAggregationDefinition = copy(size = size.some)

  override def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T = copy(pipelines = pipelines.toSeq)
  override def subAggregations(aggs: Iterable[AggregationDefinition]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
