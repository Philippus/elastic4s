package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.requests.searches.aggs.builders.{
  AdjacencyMatrixAggregationBuilder,
  AutoDateHistogramAggregationBuilder,
  AvgAggregationBuilder,
  CardinalityAggregationBuilder,
  ChildrenAggregationBuilder,
  CompositeAggregationBuilder,
  DateHistogramAggregationBuilder,
  DateRangeAggregationBuilder,
  ExtendedStatsAggregationBuilder,
  FilterAggregationBuilder,
  FiltersAggregationBuilder,
  GeoBoundsAggregationBuilder,
  GeoCentroidAggregationBuilder,
  GeoDistanceAggregationBuilder,
  GeoHashGridAggregationBuilder,
  GeoTileGridAggregationBuilder,
  GlobalAggregationBuilder,
  HistogramAggregationBuilder,
  IpRangeAggregationBuilder,
  KeyedFiltersAggregationBuilder,
  MaxAggregationBuilder,
  MinAggregationBuilder,
  MissingAggregationBuilder,
  MultiTermsAggregationBuilder,
  NestedAggregationBuilder,
  PercentilesAggregationBuilder,
  RangeAggregationBuilder,
  ReverseNestedAggregationBuilder,
  SamplerAggregationBuilder,
  ScriptedMetricAggregationBuilder,
  SigTermsAggregationBuilder,
  SigTextAggregationBuilder,
  StatsAggregationBuilder,
  SumAggregationBuilder,
  TermsAggregationBuilder,
  TopHitsAggregationBuilder,
  TopMetricsAggregationBuilder,
  ValueCountAggregationBuilder,
  VariableWidthAggregationBuilder,
  WeightedAvgAggregationBuilder
}
import com.sksamuel.elastic4s.requests.searches.aggs.pipeline._

object AggregationBuilderFn {
  def apply(
      agg: AbstractAggregation,
      customAggregationHandler: PartialFunction[AbstractAggregation, XContentBuilder]
  ): XContentBuilder = {
    val builder = agg match {

      case agg: AdjacencyMatrixAggregation   => AdjacencyMatrixAggregationBuilder(agg, customAggregationHandler)
      case agg: AutoDateHistogramAggregation => AutoDateHistogramAggregationBuilder(agg, customAggregationHandler)
      case agg: AvgAggregation               => AvgAggregationBuilder(agg)
      case agg: CardinalityAggregation       => CardinalityAggregationBuilder(agg)
      case agg: ChildrenAggregation          => ChildrenAggregationBuilder(agg, customAggregationHandler)
      case agg: CompositeAggregation         => CompositeAggregationBuilder(agg, customAggregationHandler)
      case agg: DateHistogramAggregation     => DateHistogramAggregationBuilder(agg, customAggregationHandler)
      case agg: ExtendedStatsAggregation     => ExtendedStatsAggregationBuilder(agg, customAggregationHandler)
      case agg: FilterAggregation            => FilterAggregationBuilder(agg, customAggregationHandler)
      case agg: FiltersAggregation           => FiltersAggregationBuilder(agg, customAggregationHandler)
      case agg: KeyedFiltersAggregation      => KeyedFiltersAggregationBuilder(agg, customAggregationHandler)
      case agg: GeoCentroidAggregation       => GeoCentroidAggregationBuilder(agg)
      case agg: GeoBoundsAggregation         => GeoBoundsAggregationBuilder(agg)
      case agg: GeoDistanceAggregation       => GeoDistanceAggregationBuilder(agg, customAggregationHandler)
      case agg: GeoHashGridAggregation       => GeoHashGridAggregationBuilder(agg, customAggregationHandler)
      case agg: GeoTileGridAggregation       => GeoTileGridAggregationBuilder(agg, customAggregationHandler)
      case agg: GlobalAggregation            => GlobalAggregationBuilder(agg, customAggregationHandler)
      case agg: HistogramAggregation         => HistogramAggregationBuilder(agg, customAggregationHandler)
      case agg: IpRangeAggregation           => IpRangeAggregationBuilder(agg, customAggregationHandler)
      case agg: MaxAggregation               => MaxAggregationBuilder(agg)
      case agg: MinAggregation               => MinAggregationBuilder(agg)
      case agg: MissingAggregation           => MissingAggregationBuilder(agg, customAggregationHandler)
      case agg: MultiTermsAggregation        => MultiTermsAggregationBuilder(agg, customAggregationHandler)
      case agg: NestedAggregation            => NestedAggregationBuilder(agg, customAggregationHandler)
      case agg: PercentilesAggregation       => PercentilesAggregationBuilder(agg, customAggregationHandler)
      case agg: ReverseNestedAggregation     => ReverseNestedAggregationBuilder(agg, customAggregationHandler)
      case agg: SamplerAggregation           => SamplerAggregationBuilder(agg, customAggregationHandler)
      case agg: ScriptedMetricAggregation    => ScriptedMetricAggregationBuilder(agg, customAggregationHandler)
      case agg: SigTermsAggregation          => SigTermsAggregationBuilder(agg, customAggregationHandler)
      case agg: SigTextAggregation           => SigTextAggregationBuilder(agg, customAggregationHandler)
      case agg: StatsAggregation             => StatsAggregationBuilder(agg)
      case agg: SumAggregation               => SumAggregationBuilder(agg)
      case agg: TermsAggregation             => TermsAggregationBuilder(agg, customAggregationHandler)
      case agg: TopHitsAggregation           => TopHitsAggregationBuilder(agg)
      case agg: TopMetricsAggregation        => TopMetricsAggregationBuilder(agg)
      case agg: ValueCountAggregation        => ValueCountAggregationBuilder(agg)
      case agg: RangeAggregation             => RangeAggregationBuilder(agg, customAggregationHandler)
      case agg: DateRangeAggregation         => DateRangeAggregationBuilder(agg, customAggregationHandler)
      case agg: WeightedAvgAggregation       => WeightedAvgAggregationBuilder(agg)
      case agg: VariableWidthAggregation     => VariableWidthAggregationBuilder(agg, customAggregationHandler)

      // pipeline aggs
      case agg: AvgBucketPipelineAgg             => AvgBucketPipelineAggBuilder(agg)
      case agg: BucketScriptPipelineAgg          => BucketScriptPipelineAggBuilder(agg)
      case agg: BucketSelectorPipelineAgg        => BucketSelectorPipelineBuilder(agg)
      case agg: BucketSortPipelineAgg            => BucketSortPipelineAggBuilder(agg)
      case agg: CumulativeSumPipelineAgg         => CumulativeSumPipelineAggBuilder(agg)
      case agg: CumulativeCardinalityPipelineAgg => CumulativeCardinalityPipelineAggBuilder(agg)
      case agg: DerivativePipelineAgg            => DerivativePipelineAggBuilder(agg)
      case agg: DiffPipelineAgg                  => SerialDiffPipelineAggBuilder(agg)
      case agg: ExtendedStatsBucketPipelineAgg   => ExtendedStatsBucketPipelineAggBuilder(agg)
      case agg: MaxBucket                        => MaxBucketPipelineAggBuilder(agg)
      case agg: MinBucketPipelineAgg             => MinBucketPipelineAggBuilder(agg)
      case agg: MovFnPipelineAgg                 => MovFnPipelineAggBuilder(agg)
      case agg: PercentilesBucketPipelineAgg     => PercentilesBucketPipelineAggBuilder(agg)
      case agg: SumBucketPipelineAgg             => SumBucketPipelineAggBuilder(agg)
      case agg: StatsBucketPipelineAgg           => StatsBucketPipelineAggBuilder(agg)

      case other => customAggregationHandler(other)
    }
    builder
  }
}

object DerivativePipelineAggBuilder {
  def apply(agg: DerivativePipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("derivative")
    builder.field("buckets_path", agg.bucketsPath)
    agg.unit.map(_.toSeconds).map(DateHistogramInterval.seconds).foreach(i => builder.field("unit", i.interval))
    agg.gapPolicy.foreach(policy => builder.field("gap_policy", policy.toString.toLowerCase))
    agg.format.foreach(f => builder.field("format", f))
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}

object CumulativeSumPipelineAggBuilder {
  def apply(agg: CumulativeSumPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("cumulative_sum")
    builder.field("buckets_path", agg.bucketsPath)
    agg.format.foreach(f => builder.field("format", f))
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}

object CumulativeCardinalityPipelineAggBuilder {
  def apply(agg: CumulativeCardinalityPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("cumulative_cardinality")
    builder.field("buckets_path", agg.bucketsPath)
    agg.format.foreach(f => builder.field("format", f))
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}

object MaxBucketPipelineAggBuilder {
  def apply(agg: MaxBucket): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("max_bucket")
    builder.field("buckets_path", agg.bucketsPath)
    builder.endObject().endObject()
  }
}

object SumBucketPipelineAggBuilder {
  def apply(agg: SumBucketPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("sum_bucket")
    builder.field("buckets_path", agg.bucketsPath)
    builder.endObject().endObject()
  }
}

object AggMetaDataFn {
  def apply(agg: AbstractAggregation, builder: XContentBuilder): Unit =
    if (agg.metadata.nonEmpty) {
      builder.startObject("meta")
      agg.metadata.foreach { case (key, value) => builder.autofield(key, value) }
      builder.endObject()
    }
}

object SubAggsBuilderFn {
  def apply(
      agg: Aggregation,
      builder: XContentBuilder,
      customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]
  ): Unit =
    if (agg.subaggs.nonEmpty) {
      builder.startObject("aggs")
      agg.subaggs.foreach { subagg =>
        builder.rawField(subagg.name, AggregationBuilderFn(subagg, customAggregations))
      }
      builder.endObject()
    }
}

object BucketScriptPipelineAggBuilder {
  def apply(agg: BucketScriptPipelineAgg): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("bucket_script")
    builder.startObject("buckets_path")
    agg.bucketsPaths.foreach { case (k, v) => builder.field(k, v) }
    builder.endObject()
    agg.format.foreach(builder.field("format", _))
    builder.field("script", agg.script.script)
    builder.endObject().endObject()
  }
}
