package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline._
import org.elasticsearch.search.aggregations.{AggregationBuilder, PipelineAggregationBuilder}

object AggregationBuilder {
  def apply(agg: AggregationDefinition): AggregationBuilder = agg match {
    case agg: AvgAggregationDefinition => AvgAggregationBuilder(agg)
    case agg: CardinalityAggregationDefinition => CardinalityAggregationBuilder(agg)
    case agg: ChildrenAggregationDefinition => ChildrenAggregationBuilder(agg)
    case agg: DateHistogramAggregation => DateHistogramBuilder(agg)
    case agg: DateRangeAggregation => DateRangeBuilder(agg)
    case agg: ExtendedStatsAggregationDefinition => ExtendedStatsAggregationBuilder(agg)
    case agg: FilterAggregationDefinition => FilterAggregationBuilder(agg)
    case agg: FiltersAggregationDefinition => FiltersAggregationBuilder(agg)
    case agg: KeyedFiltersAggregationDefinition => KeyedFiltersAggregationBuilder(agg)
    case agg: GeoBoundsAggregationDefinition => GeoBoundsAggregationBuilder(agg)
    case agg: GeoCentroidAggregationDefinition => GeoCentroidAggregationBuilder(agg)
    case agg: GeoDistanceAggregationDefinition => GeoDistanceAggregationBuilder(agg)
    case agg: GeoHashGridAggregationDefinition => GeoHashGridAggregationBuilder(agg)
    case agg: GlobalAggregationDefinition => GlobalAggregationBuilder(agg)
    case agg: HistogramAggregation => HistogramAggregationBuilder(agg)
    case agg: IpRangeAggregationDefinition => IpRangeAggregationBuilder(agg)
    case agg: MaxAggregationDefinition => MaxAggregationBuilder(agg)
    case agg: MinAggregationDefinition => MinAggregationBuilder(agg)
    case agg: MissingAggregationDefinition => MissingAggregationBuilder(agg)
    case agg: NestedAggregationDefinition => NestedAggregationBuilder(agg)
    case agg: PercentileRanksAggregationDefinition => PercentileRanksAggregationBuilder(agg)
    case agg: PercentilesAggregationDefinition => PercentilesAggregationBuilder(agg)
    case agg: RangeAggregationDefinition => RangeAggregationBuilder(agg)
    case agg: ReverseNestedAggregationDefinition => ReverseNestedAggregationBuilder(agg)
    case agg: ScriptedMetricAggregationDefinition => ScriptedMetricAggregationBuilder(agg)
    case agg: SigTermsAggregationDefinition => SigTermsAggregationBuilder(agg)
    case agg: StatsAggregationDefinition => StatsAggregationBuilder(agg)
    case agg: SumAggregationDefinition => SumAggregationBuilder(agg)
    case agg: TermsAggregationDefinition => TermsAggregationBuilder(agg)
    case agg: TopHitsAggregationDefinition => TopHitsAggregationBuilder(agg)
    case agg: ValueCountAggregationDefinition => ValueCountAggregationBuilder(agg)
  }
}
