package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.aggs.{
  AdjacencyMatrixAggregation,
  AutoDateHistogramAggregation,
  AvgAggregation,
  CardinalityAggregation,
  ChildrenAggregation,
  DateHistogramAggregation,
  DateRangeAggregation,
  ExtendedStatsAggregation,
  FilterAggregation,
  FiltersAggregation,
  GeoBoundsAggregation,
  GeoCentroidAggregation,
  GeoDistanceAggregation,
  GeoHashGridAggregation,
  GeoTileGridAggregation,
  GlobalAggregation,
  HistogramAggregation,
  IpRangeAggregation,
  KeyedFiltersAggregation,
  MaxAggregation,
  MinAggregation,
  MissingAggregation,
  MultiTermsAggregation,
  NestedAggregation,
  PercentileRanksAggregation,
  PercentilesAggregation,
  RangeAggregation,
  ReverseNestedAggregation,
  SamplerAggregation,
  ScriptedMetricAggregation,
  SigTermsAggregation,
  StatsAggregation,
  SumAggregation,
  TermsAggregation,
  TopHitsAggregation,
  TopMetricsAggregation,
  ValueCountAggregation,
  VariableWidthAggregation,
  WeightedAvgAggregation,
  WeightedAvgField
}
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.ext.OptionImplicits._

trait AggregationApi {

  def adjacencyMatrixAgg(name: String, filters: Iterable[(String, Query)]): AdjacencyMatrixAggregation =
    AdjacencyMatrixAggregation(name, filters)

  def avgAgg(name: String, field: String): AvgAggregation = AvgAggregation(name).field(field)

  def cardinalityAgg(name: String, field: String): CardinalityAggregation = CardinalityAggregation(name).field(field)

  def childrenAggregation(name: String, childType: String): ChildrenAggregation = ChildrenAggregation(name, childType)

  def dateHistogramAgg(name: String, field: String): DateHistogramAggregation =
    DateHistogramAggregation(name).field(field)

  def autoDateHistogramAgg(name: String, field: String): AutoDateHistogramAggregation =
    AutoDateHistogramAggregation(name, field)

  def dateRangeAgg(name: String, field: String): DateRangeAggregation = DateRangeAggregation(name).field(field)

  def extendedStatsAgg(name: String, field: String): ExtendedStatsAggregation =
    ExtendedStatsAggregation(name, field = field.some)

  def filterAgg(name: String, query: Query): FilterAggregation = FilterAggregation(name, query)

  def filtersAggregation(name: String) = new FiltersAggregationExpectsQueries(name)
  class FiltersAggregationExpectsQueries(name: String) {
    def queries(first: Query, rest: Query*): FiltersAggregation                          = queries(first +: rest)
    def queries(queries: Iterable[Query]): FiltersAggregation                            =
      FiltersAggregation(name, queries)
    def queries(first: (String, Query), rest: (String, Query)*): KeyedFiltersAggregation =
      queries(first +: rest)
    def queries(queries: Iterable[(String, Query)]): KeyedFiltersAggregation             =
      KeyedFiltersAggregation(name, queries)
  }
  def geoBoundsAggregation(name: String): GeoBoundsAggregation = GeoBoundsAggregation(name)

  def geoDistanceAggregation(name: String) = new GeoDistanceAggregationExpectsOrigin(name)
  class GeoDistanceAggregationExpectsOrigin(name: String) {
    def origin(lat: Double, long: Double): GeoDistanceAggregation = origin(GeoPoint(lat, long))
    def origin(origin: GeoPoint): GeoDistanceAggregation          = GeoDistanceAggregation(name, origin)
  }

  def geoHashGridAggregation(name: String): GeoHashGridAggregation = GeoHashGridAggregation(name)
  def geoTileGridAggregation(name: String): GeoTileGridAggregation = GeoTileGridAggregation(name)
  def geoCentroidAggregation(name: String): GeoCentroidAggregation = GeoCentroidAggregation(name)
  def globalAggregation(name: String): GlobalAggregation           = GlobalAggregation(name)
  def histogramAggregation(name: String): HistogramAggregation     = HistogramAggregation(name)
  def ipRangeAggregation(name: String): IpRangeAggregation         = IpRangeAggregation(name)

  def maxAgg(name: String, field: String): MaxAggregation = MaxAggregation(name).field(field)

  def minAgg(name: String, field: String): MinAggregation = MinAggregation(name).field(field)

  def missingAgg(name: String, field: String): MissingAggregation = MissingAggregation(name).field(field)

  def multiTermsAgg(name: String, terms: MultiTermsAggregation.Term*): MultiTermsAggregation =
    MultiTermsAggregation(name).terms(terms)

  def nestedAggregation(name: String, path: String): NestedAggregation = NestedAggregation(name, path)

  def percentilesAgg(name: String, field: String): PercentilesAggregation = PercentilesAggregation(name).field(field)

  def percentileRanksAggregation(name: String): PercentileRanksAggregation = PercentileRanksAggregation(name)

  def rangeAgg(name: String, field: String): RangeAggregation = RangeAggregation(name).field(field)

  def reverseNestedAggregation(name: String): ReverseNestedAggregation = ReverseNestedAggregation(name)

  def scriptedMetricAggregation(name: String): ScriptedMetricAggregation = ScriptedMetricAggregation(name)

  def sigTermsAggregation(name: String): SigTermsAggregation = SigTermsAggregation(name)
  def statsAggregation(name: String): StatsAggregation       = StatsAggregation(name)

  def sumAgg(name: String, field: String): SumAggregation = SumAggregation(name).field(field)

  def termsAgg(name: String, field: String): TermsAggregation = TermsAggregation(name).field(field)

  def topHitsAgg(name: String): TopHitsAggregation = TopHitsAggregation(name)

  def topMetricsAgg(name: String): TopMetricsAggregation = TopMetricsAggregation(name)

  def valueCountAgg(name: String, field: String): ValueCountAggregation = ValueCountAggregation(name).field(field)

  def weightedAvgAgg(name: String, value: WeightedAvgField, weight: WeightedAvgField): WeightedAvgAggregation =
    WeightedAvgAggregation(name).value(value).weight(weight)

  def samplerAgg(name: String): SamplerAggregation = SamplerAggregation(name)

  def variableWidthHistogramAgg(name: String, field: String): VariableWidthAggregation =
    VariableWidthAggregation(name, field)
}
