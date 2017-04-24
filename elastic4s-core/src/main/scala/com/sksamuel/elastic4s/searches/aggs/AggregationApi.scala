package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import org.elasticsearch.common.geo.GeoPoint

trait AggregationApi {

  def avgAgg(name: String, field:String): AvgAggregationDefinition = AvgAggregationDefinition(name).field(field)
  def avgAggregation(name: String): AvgAggregationDefinition = AvgAggregationDefinition(name)

  def cardinalityAgg(name: String, field: String): CardinalityAggregationDefinition = CardinalityAggregationDefinition(name).field(field)
  def cardinalityAggregation(name: String): CardinalityAggregationDefinition = CardinalityAggregationDefinition(name)

  def childrenAggregation(name: String, childType: String): ChildrenAggregationDefinition =
    ChildrenAggregationDefinition(name, childType)

  def dateHistogramAgg(name: String, field: String): DateHistogramAggregation = dateHistogramAggregation(name).field(field)
  def dateHistogramAggregation(name: String): DateHistogramAggregation = DateHistogramAggregation(name)

  def dateRangeAggregation(name: String): DateRangeAggregation = DateRangeAggregation(name)
  def extendedStatsAggregation(name: String): ExtendedStatsAggregationDefinition = ExtendedStatsAggregationDefinition(
    name)

  def filterAgg(name: String, query: QueryDefinition) = FilterAggregationDefinition(name, query)
  def filterAggregation(name: String) = new FilterAggregationExpectsQuery(name)
  class FilterAggregationExpectsQuery(name: String) {
    @deprecated("use query", "5.0.0")
    def filter(query: QueryDefinition) = FilterAggregationDefinition(name, query)
    def query(query: QueryDefinition) = FilterAggregationDefinition(name, query)
  }

  def filtersAggregation(name: String) = new FiltersAggregationExpectsQueries(name)
  class FiltersAggregationExpectsQueries(name: String) {
    def queries(first: QueryDefinition, rest: QueryDefinition*): FiltersAggregationDefinition = queries(first +: rest)
    def queries(queries: Iterable[QueryDefinition]): FiltersAggregationDefinition =
      FiltersAggregationDefinition(name, queries)
    def queries(first: (String, QueryDefinition), rest: (String, QueryDefinition)*): KeyedFiltersAggregationDefinition = queries(first +: rest)
    def queries(queries: Iterable[(String, QueryDefinition)]): KeyedFiltersAggregationDefinition =
      KeyedFiltersAggregationDefinition(name, queries)
  }
  def geoBoundsAggregation(name: String): GeoBoundsAggregationDefinition = GeoBoundsAggregationDefinition(name)

  def geoDistanceAggregation(name: String) = new GeoDistanceAggregationExpectsOrigin(name)
  class GeoDistanceAggregationExpectsOrigin(name: String) {
    def origin(lat: Double, long: Double): GeoDistanceAggregationDefinition = origin(new GeoPoint(lat, long))
    def origin(origin: GeoPoint): GeoDistanceAggregationDefinition = GeoDistanceAggregationDefinition(name, origin)
  }

  def geoHashGridAggregation(name: String): GeoHashGridAggregationDefinition = GeoHashGridAggregationDefinition(name)
  def geoCentroidAggregation(name: String): GeoCentroidAggregationDefinition = GeoCentroidAggregationDefinition(name)
  def globalAggregation(name: String): GlobalAggregationDefinition = GlobalAggregationDefinition(name)
  def histogramAggregation(name: String): HistogramAggregation = HistogramAggregation(name)
  def ipRangeAggregation(name: String): IpRangeAggregationDefinition = IpRangeAggregationDefinition(name)

  def maxAgg(name: String, field: String): MaxAggregationDefinition = MaxAggregationDefinition(name).field(field)
  def maxAggregation(name: String): MaxAggregationDefinition = MaxAggregationDefinition(name)

  def minAgg(name: String, field: String): MinAggregationDefinition = MinAggregationDefinition(name).field(field)
  def minAggregation(name: String): MinAggregationDefinition = MinAggregationDefinition(name)

  def missingAgg(name: String, field: String): MissingAggregationDefinition = MissingAggregationDefinition(name).field(field)
  def missingAggregation(name: String): MissingAggregationDefinition = MissingAggregationDefinition(name)

  def nestedAggregation(name: String, path: String): NestedAggregationDefinition =
    NestedAggregationDefinition(name, path)

  def percentilesAggregation(name: String): PercentilesAggregationDefinition =
    PercentilesAggregationDefinition(name)

  def percentileRanksAggregation(name: String): PercentileRanksAggregationDefinition =
    PercentileRanksAggregationDefinition(name)

  def rangeAggregation(name: String): RangeAggregationDefinition = RangeAggregationDefinition(name)

  def reverseNestedAggregation(name: String): ReverseNestedAggregationDefinition =
    ReverseNestedAggregationDefinition(name)

  def scriptedMetricAggregation(name: String): ScriptedMetricAggregationDefinition =
    ScriptedMetricAggregationDefinition(name)

  def sigTermsAggregation(name: String): SigTermsAggregationDefinition = SigTermsAggregationDefinition(name)
  def statsAggregation(name: String): StatsAggregationDefinition = StatsAggregationDefinition(name)

  def sumAggregation(name: String): SumAggregationDefinition = SumAggregationDefinition(name)
  def sumAgg(name: String, field: String): SumAggregationDefinition = SumAggregationDefinition(name).field(field)

  def termsAggregation(name: String): TermsAggregationDefinition = TermsAggregationDefinition(name)
  def termsAgg(name: String, field: String): TermsAggregationDefinition = TermsAggregationDefinition(name).field(field)

  def topHitsAggregation(name: String): TopHitsAggregationDefinition = TopHitsAggregationDefinition(name)

  def valueCountAgg(name: String, field: String): ValueCountAggregationDefinition = ValueCountAggregationDefinition(name).field(field)
  def valueCountAggregation(name: String): ValueCountAggregationDefinition = ValueCountAggregationDefinition(name)
}
