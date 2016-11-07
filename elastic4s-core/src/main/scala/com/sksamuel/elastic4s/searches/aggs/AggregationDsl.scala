package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.common.geo.GeoPoint

trait AggregationDsl {

  def avgAggregation(name: String): AvgAggregationDefinition = AvgAggregationDefinition(name)

  def cardinalityAggregation(name: String): CardinalityAggregationDefinition = CardinalityAggregationDefinition(name)

  def childrenAggregation(name: String, childType: String): ChildrenAggregationDefinition =
    ChildrenAggregationDefinition(name, childType)

  def dateHistogramAggregation(name: String): DateHistogramAggregation = DateHistogramAggregation(name)
  def dateRangeAggregation(name: String): DateRangeAggregation = DateRangeAggregation(name)
  def extendedStatsAggregation(name: String): ExtendedStatsAggregationDefinition = ExtendedStatsAggregationDefinition(
    name)

  def filterAggregation(name: String) = new FilterAggregationExpectsQuery(name)
  class FilterAggregationExpectsQuery(name: String) {
    def query(query: QueryDefinition) = FilterAggregationDefinition(name, query)
  }

  def filtersAggregation(name: String) = new FiltersAggregationExpectsQueries(name)
  class FiltersAggregationExpectsQueries(name: String) {
    def queries(first: QueryDefinition, rest: QueryDefinition*): FiltersAggregationDefinition = queries(first +: rest)
    def queries(queries: Iterable[QueryDefinition]): FiltersAggregationDefinition =
      FiltersAggregationDefinition(name, queries)
  }
  def geoBoundsAggregation(name: String): GeoBoundsAggregationDefinition = GeoBoundsAggregationDefinition(name)

  def geoDistanceAggregation(name: String) = new GeoDistanceAggregationExpectsOrigin(name)
  class GeoDistanceAggregationExpectsOrigin(name: String) {
    def origin(origin: GeoPoint): GeoDistanceAggregationDefinition = GeoDistanceAggregationDefinition(name, origin)
  }

  def geoHashGridAggregation(name: String): GeoHashGridAggregationDefinition = GeoHashGridAggregationDefinition(name)
  def geoCentroidAggregation(name: String): GeoCentroidAggregationDefinition = GeoCentroidAggregationDefinition(name)
  def globalAggregation(name: String): GlobalAggregationDefinition = GlobalAggregationDefinition(name)
  def histogramAggregation(name: String): HistogramAggregation = HistogramAggregation(name)
  def ipRangeAggregation(name: String): IpRangeAggregationDefinition = IpRangeAggregationDefinition(name)

  def maxAggregation(name: String): MaxAggregationDefinition = MaxAggregationDefinition(name)
  def minAggregation(name: String): MinAggregationDefinition = MinAggregationDefinition(name)

  def missingAggregation(name: String): MissingAggregationDefinition = MissingAggregationDefinition(name)

  def nestedAggregation(name: String, path: String): NestedAggregationDefinition =
    NestedAggregationDefinition(name, path)

  def percentilesAggregation(name: String): PercentilesAggregationDefinition = PercentilesAggregationDefinition(name)
  def percentileRanksAggregation(name: String): PercentileRanksAggregationDefinition = PercentileRanksAggregationDefinition(
    name)
  def rangeAggregation(name: String): RangeAggregationDefinition = RangeAggregationDefinition(name)

  def reverseNestedAggregation(name: String): ReverseNestedAggregationDefinition =
    ReverseNestedAggregationDefinition(name)

  def scriptedMetricAggregation(name: String): ScriptedMetricAggregationDefinition =
    ScriptedMetricAggregationDefinition(name)

  def sigTermsAggregation(name: String): SigTermsAggregationDefinition = SigTermsAggregationDefinition(name)
  def statsAggregation(name: String): StatsAggregationDefinition = StatsAggregationDefinition(name)
  def sumAggregation(name: String): SumAggregationDefinition = SumAggregationDefinition(name)
  def termsAggregation(name: String): TermsAggregationDefinition = TermsAggregationDefinition(name)
  def topHitsAggregation(name: String): TopHitsAggregationDefinition = TopHitsAggregationDefinition(name)

  def valueCountAggregation(name: String): ValueCountAggregationDefinition = ValueCountAggregationDefinition(name)
}
