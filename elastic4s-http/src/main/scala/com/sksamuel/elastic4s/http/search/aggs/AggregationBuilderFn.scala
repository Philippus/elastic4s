package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.DateHistogramInterval
import com.sksamuel.elastic4s.searches.aggs._
import com.sksamuel.elastic4s.searches.aggs.pipeline._
import com.sksamuel.elastic4s.http.search.aggs.pipeline.BucketSelectorPipelineBuilder

object AggregationBuilderFn {
  def apply(agg: AbstractAggregation): XContentBuilder = {
    val builder = agg match {

      case agg: AvgAggregationDefinition => AvgAggregationBuilder(agg)
      case agg: CardinalityAggregationDefinition => CardinalityAggregationBuilder(agg)
      case agg: ChildrenAggregationDefinition => ChildrenAggregationBuilder(agg)
      case agg: DateHistogramAggregation => DateHistogramAggregationBuilder(agg)
      case agg: ExtendedStatsAggregationDefinition => ExtendedStatsAggregationBuilder(agg)
      case agg: FilterAggregationDefinition => FilterAggregationBuilder(agg)
      case agg: FiltersAggregationDefinition => FiltersAggregationBuilder(agg)
      case agg: KeyedFiltersAggregationDefinition => KeyedFiltersAggregationBuilder(agg)
      case agg: GeoCentroidAggregationDefinition => GeoCentroidAggregationBuilder(agg)
      case agg: GeoBoundsAggregationDefinition => GeoBoundsAggregationBuilder(agg)
      case agg: GeoDistanceAggregationDefinition => GeoDistanceAggregationBuilder(agg)
      case agg: GeoHashGridAggregationDefinition => GeoHashGridAggregationBuilder(agg)
      case agg: HistogramAggregation => HistogramAggregationBuilder(agg)
      case agg: IpRangeAggregationDefinition => IpRangeAggregationBuilder(agg)
      case agg: MaxAggregationDefinition => MaxAggregationBuilder(agg)
      case agg: MinAggregationDefinition => MinAggregationBuilder(agg)
      case agg: MissingAggregationDefinition => MissingAggregationBuilder(agg)
      case agg: NestedAggregationDefinition => NestedAggregationBuilder(agg)
      case agg: PercentilesAggregationDefinition => PercentilesAggregationBuilder(agg)
      case agg: SamplerAggregationDefinition => SamplerAggregationBuilder(agg)
      case agg: SigTermsAggregationDefinition => SigTermsAggregationBuilder(agg)
      case agg: SigTextAggregationDefinition => SigTextAggregationBuilder(agg)
      case agg: StatsAggregationDefinition => StatsAggregationBuilder(agg)
      case agg: SumAggregationDefinition => SumAggregationBuilder(agg)
      case agg: TopHitsAggregationDefinition => TopHitsAggregationBuilder(agg)
      case agg: TermsAggregationDefinition => TermsAggregationBuilder(agg)
      case agg: ValueCountAggregationDefinition => ValueCountAggregationBuilder(agg)

      case agg: RangeAggregationDefinition => RangeAggregationBuilder(agg)
      case agg: DateRangeAggregation => DateRangeAggregationBuilder(agg)

      // pipeline aggs
      case agg: BucketSelectorDefinition => BucketSelectorPipelineBuilder(agg)
      case agg: DerivativeDefinition => DerivativePipelineAggBuilder(agg)
      case agg: MaxBucketDefinition => MaxBucketPipelineAggBuilder(agg)
      case agg: SumBucketDefinition => SumBucketPipelineAggBuilder(agg)
      case agg: BucketScriptDefinition => BucketScriptPipelineAggBuilder(agg)
      case agg: CumulativeSumDefinition => CumulativeSumPipelineAggBuilder(agg)

      // Not implemented
      case ni => throw new NotImplementedError(s"Aggregation ${ni.getClass.getName} has not yet been implemented for the HTTP client.")
    }
    builder
  }
}

object DerivativePipelineAggBuilder {
  def apply(agg: DerivativeDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("derivative")
    builder.field("buckets_path", agg.bucketsPath)
    agg.unit.map(_.toSeconds).map(DateHistogramInterval.seconds).foreach(i=>builder.field("unit", i.interval))
    agg.gapPolicy.foreach(policy=> builder.field("gap_policy", policy.toString.toLowerCase))
    agg.format.foreach(f=>builder.field("format", f))
    builder.endObject()
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}


object CumulativeSumPipelineAggBuilder {
  def apply(agg: CumulativeSumDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("cumulative_sum")
    builder.field("buckets_path", agg.bucketsPath)
    agg.format.foreach(f=>builder.field("format", f))
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
  def apply(agg: AbstractAggregation, builder: XContentBuilder): Unit = {
    if (agg.metadata.nonEmpty) {
      builder.startObject("meta")
      agg.metadata.foreach { case (key, value) => builder.autofield(key, value) }
      builder.endObject()
    }
  }
}

object SubAggsBuilderFn {
  def apply(agg: AggregationDefinition, builder: XContentBuilder): Unit = {
    if (agg.subaggs.nonEmpty) {
      builder.startObject("aggs")
      agg.subaggs.foreach { subagg =>
        builder.rawField(subagg.name, AggregationBuilderFn(subagg))
      }
      builder.endObject()
    }
  }
}

object BucketScriptPipelineAggBuilder {
  def apply(agg: BucketScriptDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("bucket_script")
    builder.startObject("buckets_path")
    agg.bucketsPaths.foreach { case (k,v) => builder.field(k, v)}
    builder.endObject()
    agg.format.foreach(builder.field("format", _))
    builder.field("script", agg.script.script)
    builder.endObject().endObject()
  }
}
