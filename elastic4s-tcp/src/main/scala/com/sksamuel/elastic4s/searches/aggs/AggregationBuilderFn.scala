package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline._
import cats.syntax.either._
import org.elasticsearch.search.aggregations.{AggregationBuilder, PipelineAggregationBuilder}

object AggregationBuilderFn {

  def apply(agg: AbstractAggregation): Either[AggregationBuilder, PipelineAggregationBuilder] = agg match {

    // regular aggs
    case agg: AvgAggregationDefinition => AvgAggregationBuilder(agg).asLeft
    case agg: CardinalityAggregationDefinition => CardinalityAggregationBuilder(agg).asLeft
    case agg: ChildrenAggregationDefinition => ChildrenAggregationBuilder(agg).asLeft
    case agg: DateHistogramAggregation => DateHistogramBuilder(agg).asLeft
    case agg: DateRangeAggregation => DateRangeBuilder(agg).asLeft
    case agg: ExtendedStatsAggregationDefinition => ExtendedStatsAggregationBuilder(agg).asLeft
    case agg: FilterAggregationDefinition => FilterAggregationBuilder(agg).asLeft
    case agg: FiltersAggregationDefinition => FiltersAggregationBuilder(agg).asLeft
    case agg: KeyedFiltersAggregationDefinition => KeyedFiltersAggregationBuilder(agg).asLeft
    case agg: GeoBoundsAggregationDefinition => GeoBoundsAggregationBuilder(agg).asLeft
    case agg: GeoCentroidAggregationDefinition => GeoCentroidAggregationBuilder(agg).asLeft
    case agg: GeoDistanceAggregationDefinition => GeoDistanceAggregationBuilder(agg).asLeft
    case agg: GeoHashGridAggregationDefinition => GeoHashGridAggregationBuilder(agg).asLeft
    case agg: GlobalAggregationDefinition => GlobalAggregationBuilder(agg).asLeft
    case agg: HistogramAggregation => HistogramAggregationBuilder(agg).asLeft
    case agg: IpRangeAggregationDefinition => IpRangeAggregationBuilder(agg).asLeft
    case agg: MaxAggregationDefinition => MaxAggregationBuilder(agg).asLeft
    case agg: MinAggregationDefinition => MinAggregationBuilder(agg).asLeft
    case agg: MissingAggregationDefinition => MissingAggregationBuilder(agg).asLeft
    case agg: NestedAggregationDefinition => NestedAggregationBuilder(agg).asLeft
    case agg: PercentileRanksAggregationDefinition => PercentileRanksAggregationBuilder(agg).asLeft
    case agg: PercentilesAggregationDefinition => PercentilesAggregationBuilder(agg).asLeft
    case agg: RangeAggregationDefinition => RangeAggregationBuilder(agg).asLeft
    case agg: ReverseNestedAggregationDefinition => ReverseNestedAggregationBuilder(agg).asLeft
    case agg: ScriptedMetricAggregationDefinition => ScriptedMetricAggregationBuilder(agg).asLeft
    case agg: SigTermsAggregationDefinition => SigTermsAggregationBuilder(agg).asLeft
    case agg: StatsAggregationDefinition => StatsAggregationBuilder(agg).asLeft
    case agg: SumAggregationDefinition => SumAggregationBuilder(agg).asLeft
    case agg: TermsAggregationDefinition => TermsAggregationBuilder(agg).asLeft
    case agg: TopHitsAggregationDefinition => TopHitsAggregationBuilder(agg).asLeft
    case agg: ValueCountAggregationDefinition => ValueCountAggregationBuilder(agg).asLeft

    // pipeline aggs follow
    case p: AvgBucketDefinition => AvgBucketPipelineBuilder(p).asRight
    case p: CumulativeSumDefinition => CumulativeSumPipelineBuilder(p).asRight
    case p: DerivativeDefinition => DerivativePipelineBuilder(p).asRight
    case p: DiffDefinition => SerialDiffPipelineBuilder(p).asRight
    case p: ExtendedStatsBucketDefinition => ExtendedStatsBucketBuilder(p).asRight
    case p: MaxBucketDefinition => MaxBucketPipelineBuilder(p).asRight
    case p: MinBucketDefinition => MinBucketPipelineBuilder(p).asRight
    case p: MovAvgDefinition => MovAvgPipelineBuilder(p).asRight
    case p: PercentilesBucketDefinition => PercentilesBucketPipelineBuilder(p).asRight
    case p: StatsBucketDefinition => StatsBucketPipelineBuilder(p).asRight
    case p: SumBucketDefinition => SumBucketPipelineBuilder(p).asRight
  }
}
