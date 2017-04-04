package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs._
import org.elasticsearch.common.xcontent.XContentBuilder

object AggregationBuilderFn {
  def apply(agg: AggregationDefinition): XContentBuilder = {
    val builder = agg match {
      case agg: AvgAggregationDefinition => AvgAggregationBuilder(agg)
      case agg: CardinalityAggregationDefinition => CardinalityAggregationBuilder(agg)
      case agg: FilterAggregationDefinition => FilterAggregationBuilder(agg)
      case agg: MaxAggregationDefinition => MaxAggregationBuilder(agg)
      case agg: MinAggregationDefinition => MinAggregationBuilder(agg)
      case agg: MissingAggregationDefinition => MissingAggregationBuilder(agg)
      case agg: SumAggregationDefinition => SumAggregationBuilder(agg)
      case agg: TermsAggregationDefinition => TermsAggregationBuilder(agg)
      case agg: ValueCountAggregationDefinition => ValueCountAggregationBuilder(agg)
      case agg: DateHistogramAggregation => DateHistogramAggregationBuilder(agg)
    }
    builder
  }
}

object AggMetaDataFn {
  def apply(agg: AggregationDefinition, builder: XContentBuilder): Unit = {
    builder.startObject("meta")
    if (agg.metadata.nonEmpty)
      agg.metadata.foreach { case (key, value) => builder.field(key, value) }
    builder.endObject()
  }
}

object SubAggsBuilderFn {
  def apply(agg: AggregationDefinition, builder: XContentBuilder): Unit = {
    builder.startObject("aggs")
    agg.subaggs.foreach { subagg =>
      builder.rawField(subagg.name, AggregationBuilderFn(subagg).bytes)
    }
    builder.endObject()
  }
}
