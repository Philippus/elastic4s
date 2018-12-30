package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

trait AggregationApi {

  def avgAgg(name: String, field: String): AvgAggregation = AvgAggregation(name).field(field)
  def avgAggregation(name: String): AvgAggregation        = AvgAggregation(name)

  def cardinalityAgg(name: String, field: String): CardinalityAggregation =
    CardinalityAggregation(name).field(field)
  def cardinalityAggregation(name: String): CardinalityAggregation = CardinalityAggregation(name)

  def childrenAggregation(name: String, childType: String): ChildrenAggregation =
    ChildrenAggregation(name, childType)

  def dateHistogramAgg(name: String, field: String): DateHistogramAggregation =
    dateHistogramAggregation(name).field(field)
  def dateHistogramAggregation(name: String): DateHistogramAggregation = DateHistogramAggregation(name)

  def dateRangeAgg(name: String, field: String): DateRangeAggregation = dateRangeAggregation(name).field(field)
  def dateRangeAggregation(name: String): DateRangeAggregation        = DateRangeAggregation(name)

  def extendedStatsAggregation(name: String): ExtendedStatsAggregation =
    ExtendedStatsAggregation(name)

  def extendedStatsAgg(name: String, field: String) = ExtendedStatsAggregation(name, field = field.some)

  def filterAgg(name: String, query: Query) = FilterAggregation(name, query)
  def filterAggregation(name: String)       = new FilterAggregationExpectsQuery(name)
  class FilterAggregationExpectsQuery(name: String) {
    def query(query: Query) = FilterAggregation(name, query)
  }

  def filtersAggregation(name: String) = new FiltersAggregationExpectsQueries(name)
  class FiltersAggregationExpectsQueries(name: String) {
    def queries(first: Query, rest: Query*): FiltersAggregation = queries(first +: rest)
    def queries(queries: Iterable[Query]): FiltersAggregation =
      FiltersAggregation(name, queries)
    def queries(first: (String, Query), rest: (String, Query)*): KeyedFiltersAggregation =
      queries(first +: rest)
    def queries(queries: Iterable[(String, Query)]): KeyedFiltersAggregation =
      KeyedFiltersAggregation(name, queries)
  }
  def geoBoundsAggregation(name: String): GeoBoundsAggregation = GeoBoundsAggregation(name)

  def geoDistanceAggregation(name: String) = new GeoDistanceAggregationExpectsOrigin(name)
  class GeoDistanceAggregationExpectsOrigin(name: String) {
    def origin(lat: Double, long: Double): GeoDistanceAggregation = origin(GeoPoint(lat, long))
    def origin(origin: GeoPoint): GeoDistanceAggregation          = GeoDistanceAggregation(name, origin)
  }

  def geoHashGridAggregation(name: String): GeoHashGridAggregation = GeoHashGridAggregation(name)
  def geoCentroidAggregation(name: String): GeoCentroidAggregation = GeoCentroidAggregation(name)
  def globalAggregation(name: String): GlobalAggregation           = GlobalAggregation(name)
  def histogramAggregation(name: String): HistogramAggregation     = HistogramAggregation(name)
  def ipRangeAggregation(name: String): IpRangeAggregation         = IpRangeAggregation(name)

  def maxAgg(name: String, field: String): MaxAggregation = MaxAggregation(name).field(field)
  def maxAggregation(name: String): MaxAggregation        = MaxAggregation(name)

  def minAgg(name: String, field: String): MinAggregation = MinAggregation(name).field(field)
  def minAggregation(name: String): MinAggregation        = MinAggregation(name)

  def missingAgg(name: String, field: String): MissingAggregation =
    MissingAggregation(name).field(field)
  def missingAggregation(name: String): MissingAggregation = MissingAggregation(name)

  def nestedAggregation(name: String, path: String): NestedAggregation =
    NestedAggregation(name, path)

  def percentilesAgg(name: String, field: String) = PercentilesAggregation(name).field(field)
  def percentilesAggregation(name: String)        = PercentilesAggregation(name)

  def percentileRanksAggregation(name: String): PercentileRanksAggregation =
    PercentileRanksAggregation(name)

  def rangeAgg(name: String, field: String): RangeAggregation = RangeAggregation(name).field(field)
  def rangeAggregation(name: String): RangeAggregation        = RangeAggregation(name)

  def reverseNestedAggregation(name: String): ReverseNestedAggregation =
    ReverseNestedAggregation(name)

  def scriptedMetricAggregation(name: String): ScriptedMetricAggregation =
    ScriptedMetricAggregation(name)

  def sigTermsAggregation(name: String): SigTermsAggregation = SigTermsAggregation(name)
  def statsAggregation(name: String): StatsAggregation       = StatsAggregation(name)

  def sumAggregation(name: String): SumAggregation        = SumAggregation(name)
  def sumAgg(name: String, field: String): SumAggregation = SumAggregation(name).field(field)

  def termsAggregation(name: String): TermsAggregation        = TermsAggregation(name)
  def termsAgg(name: String, field: String): TermsAggregation = TermsAggregation(name).field(field)

  def topHitsAgg(name: String): TopHitsAggregation         = TopHitsAggregation(name)
  def topHitsAggregation(name: String): TopHitsAggregation = TopHitsAggregation(name)

  def valueCountAgg(name: String, field: String): ValueCountAggregation =
    ValueCountAggregation(name).field(field)
  def valueCountAggregation(name: String): ValueCountAggregation = ValueCountAggregation(name)

  def samplerAgg(name: String): SamplerAggregation         = SamplerAggregation(name)
  def samplerAggregation(name: String): SamplerAggregation = SamplerAggregation(name)
}
