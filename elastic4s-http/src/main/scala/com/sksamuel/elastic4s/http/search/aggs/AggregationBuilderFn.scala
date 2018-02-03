package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.DateHistogramInterval
import com.sksamuel.elastic4s.searches.aggs._
import com.sksamuel.elastic4s.searches.aggs.pipeline._
import com.sksamuel.elastic4s.http.search.aggs.pipeline._

object AggregationBuilderFn {
  def apply(agg: AbstractAggregation): XContentBuilder = {
    val builder = agg match {

      case agg: AvgAggregation           => AvgAggregationBuilder(agg)
      case agg: CardinalityAggregation   => CardinalityAggregationBuilder(agg)
      case agg: ChildrenAggregation      => ChildrenAggregationBuilder(agg)
      case agg: DateHistogramAggregation           => DateHistogramAggregationBuilder(agg)
      case agg: ExtendedStatsAggregation => ExtendedStatsAggregationBuilder(agg)
      case agg: FilterAggregation        => FilterAggregationBuilder(agg)
      case agg: FiltersAggregation       => FiltersAggregationBuilder(agg)
      case agg: KeyedFiltersAggregation  => KeyedFiltersAggregationBuilder(agg)
      case agg: GeoCentroidAggregation   => GeoCentroidAggregationBuilder(agg)
      case agg: GeoBoundsAggregation     => GeoBoundsAggregationBuilder(agg)
      case agg: GeoDistanceAggregation   => GeoDistanceAggregationBuilder(agg)
      case agg: GeoHashGridAggregation   => GeoHashGridAggregationBuilder(agg)
      case agg: GlobalAggregation        => GlobalAggregationBuilder(agg)
      case agg: HistogramAggregation               => HistogramAggregationBuilder(agg)
      case agg: IpRangeAggregation       => IpRangeAggregationBuilder(agg)
      case agg: MaxAggregation           => MaxAggregationBuilder(agg)
      case agg: MinAggregation           => MinAggregationBuilder(agg)
      case agg: MissingAggregation       => MissingAggregationBuilder(agg)
      case agg: NestedAggregation        => NestedAggregationBuilder(agg)
      case agg: PercentilesAggregation   => PercentilesAggregationBuilder(agg)
      case agg: ReverseNestedAggregation => ReverseNestedAggregationBuilder(agg)
      case agg: SamplerAggregation       => SamplerAggregationBuilder(agg)
      case agg: SigTermsAggregation      => SigTermsAggregationBuilder(agg)
      case agg: SigTextAggregation       => SigTextAggregationBuilder(agg)
      case agg: StatsAggregation         => StatsAggregationBuilder(agg)
      case agg: SumAggregation           => SumAggregationBuilder(agg)
      case agg: TopHitsAggregation       => TopHitsAggregationBuilder(agg)
      case agg: TermsAggregation         => TermsAggregationBuilder(agg)
      case agg: ValueCountAggregation    => ValueCountAggregationBuilder(agg)

      case agg: RangeAggregation => RangeAggregationBuilder(agg)
      case agg: DateRangeAggregation       => DateRangeAggregationBuilder(agg)

      // pipeline aggs
      case agg: AvgBucketDefinition           => AvgBucketPipelineAggBuilder(agg)
      case agg: BucketScriptDefinition        => BucketScriptPipelineAggBuilder(agg)
      case agg: BucketSelectorDefinition      => BucketSelectorPipelineBuilder(agg)
      case agg: BucketSortDefinition          => BucketSortPipelineAggBuilder(agg)
      case agg: CumulativeSumDefinition       => CumulativeSumPipelineAggBuilder(agg)
      case agg: DerivativeDefinition          => DerivativePipelineAggBuilder(agg)
      case agg: DiffDefinition                => SerialDiffPipelineAggBuilder(agg)
      case agg: ExtendedStatsBucketDefinition => ExtendedStatsBucketPipelineAggBuilder(agg)
      case agg: MaxBucketDefinition           => MaxBucketPipelineAggBuilder(agg)
      case agg: MinBucketDefinition           => MinBucketPipelineAggBuilder(agg)
      case agg: MovAvgDefinition              => MovAvgPipelineAggBuilder(agg)
      case agg: PercentilesBucketDefinition   => PercentilesBucketPipelineAggBuilder(agg)
      case agg: SumBucketDefinition           => SumBucketPipelineAggBuilder(agg)
      case agg: StatsBucketDefinition         => StatsBucketPipelineAggBuilder(agg)

      // Not implemented
      case ni =>
        throw new NotImplementedError(
          s"Aggregation ${ni.getClass.getName} has not yet been implemented for the HTTP client."
        )
    }
    builder
  }
}

object DerivativePipelineAggBuilder {
  def apply(agg: DerivativeDefinition): XContentBuilder = {
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
  def apply(agg: CumulativeSumDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("cumulative_sum")
    builder.field("buckets_path", agg.bucketsPath)
    agg.format.foreach(f => builder.field("format", f))
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}

object MaxBucketPipelineAggBuilder {
  def apply(agg: MaxBucketDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("max_bucket")
    builder.field("buckets_path", agg.bucketsPath)
    builder.endObject().endObject()
  }
}

object SumBucketPipelineAggBuilder {
  def apply(agg: SumBucketDefinition): XContentBuilder = {
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
  def apply(agg: Aggregation, builder: XContentBuilder): Unit =
    if (agg.subaggs.nonEmpty) {
      builder.startObject("aggs")
      agg.subaggs.foreach { subagg =>
        builder.rawField(subagg.name, AggregationBuilderFn(subagg))
      }
      builder.endObject()
    }
}

object BucketScriptPipelineAggBuilder {
  def apply(agg: BucketScriptDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("bucket_script")
    builder.startObject("buckets_path")
    agg.bucketsPaths.foreach { case (k, v) => builder.field(k, v) }
    builder.endObject()
    agg.format.foreach(builder.field("format", _))
    builder.field("script", agg.script.script)
    builder.endObject().endObject()
  }
}
