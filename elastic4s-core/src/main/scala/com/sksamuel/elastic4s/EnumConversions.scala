package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.common.DistanceUnit.{Centimeters, Feet, Inch, Kilometers, Meters, Miles, Millimeters, NauticalMiles, Yard}
import com.sksamuel.elastic4s.requests.searches.QueryRescoreMode.{Avg, Max, Min, Multiply, Total}
import com.sksamuel.elastic4s.requests.searches.aggs.{HistogramOrder, SubAggCollectionMode, TermsOrder}
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.{CombineFunction, FunctionScoreQueryScoreMode, MultiValueMode}
import com.sksamuel.elastic4s.requests.searches.queries.geo.GeoDistance.{Arc, Plane}
import com.sksamuel.elastic4s.requests.searches.queries.geo.{GeoDistance, GeoExecType, GeoValidationMethod}
import com.sksamuel.elastic4s.requests.searches.queries.matches.MultiMatchQueryBuilderType.{BEST_FIELDS, CROSS_FIELDS, MOST_FIELDS, PHRASE, PHRASE_PREFIX}
import com.sksamuel.elastic4s.requests.searches.queries.matches.{MultiMatchQueryBuilderType, ZeroTermsQuery}
import com.sksamuel.elastic4s.requests.searches.queries.{RegexpFlag, SimpleQueryStringFlag}
import com.sksamuel.elastic4s.requests.searches.sort.{SortMode, SortOrder}
import com.sksamuel.elastic4s.requests.searches.suggestion.{Fuzziness, SortBy, StringDistance, SuggestMode}
import com.sksamuel.elastic4s.requests.searches.{DateHistogramInterval, QueryRescoreMode, ScoreMode}
import org.joda.time.DateTimeZone

object EnumConversions {

  def regexflag(flag: RegexpFlag): String = flag.toString.toUpperCase

  def order(order: SortOrder): String = order match {
    case SortOrder.Asc  => "asc"
    case SortOrder.Desc => "desc"
  }

  def queryRescoreMode(mode: QueryRescoreMode): String = mode match {
    case Avg      => "avg"
    case Max      => "max"
    case Min      => "min"
    case Total    => "total"
    case Multiply => "multiply"
  }

  def sortMode(mode: SortMode): String = mode.toString.toLowerCase

  def geoDistance(distance: GeoDistance): String = distance match {
    case Arc   => "arc"
    case Plane => "plane"
  }

  def unit(distanceUnit: DistanceUnit): String = distanceUnit match {
    case Inch          => "in"
    case Yard          => "yd"
    case Feet          => "ft"
    case Kilometers    => "km"
    case NauticalMiles => "nmi"
    case Millimeters   => "mm"
    case Centimeters   => "cm"
    case Miles         => "mi"
    case Meters        => "m"
  }

  def order(order: TermsOrder): XContentBuilder = {
    val builder = XContentFactory.obj()
    if (order.asc)
      builder.field(order.name, "asc")
    else
      builder.field(order.name, "desc")
    builder.endObject()
  }

  def order(order: HistogramOrder): XContentBuilder = {
    val builder = XContentFactory.obj()
    if (order.asc)
      builder.field(order.name, "asc")
    else
      builder.field(order.name, "desc")
    builder.endObject()
  }

  def timeZone(zone: DateTimeZone): String = zone.getID

  def interval(interval: DateHistogramInterval): String = interval.interval

  def scoreMode(scoreMode: ScoreMode): String = scoreMode.toString.toLowerCase

  def scoreMode(scoreMode: FunctionScoreQueryScoreMode): String = scoreMode.toString.toLowerCase

  def boostMode(combineFunction: CombineFunction): String = combineFunction.toString.toLowerCase

  def geoExecType(execType: GeoExecType): String = execType.toString.toLowerCase

  def geoValidationMethod(method: GeoValidationMethod): String = method match {
    case GeoValidationMethod.Coerce          => "COERCE"
    case GeoValidationMethod.IgnoreMalformed => "IGNORE_MALFORMED"
    case GeoValidationMethod.Strict          => "STRICT"
  }

  def collectMode(mode: SubAggCollectionMode): String = mode match {
    case SubAggCollectionMode.BreadthFirst => "breadth_first"
    case SubAggCollectionMode.DepthFirst => "depth_first"
  }

  def sortBy(by: SortBy): String = by.toString.toLowerCase

  def suggestMode(mode: SuggestMode): String = mode.toString.toLowerCase

  def stringDistance(impl: StringDistance): String = impl.toString

  def simpleQueryStringFlag(flag: SimpleQueryStringFlag): String = flag.toString.toUpperCase

  def fuzziness(fuzziness: Fuzziness): String = fuzziness match {
    case Fuzziness.Zero => "0"
    case Fuzziness.One  => "1"
    case Fuzziness.Two  => "2"
    case Fuzziness.Auto => "AUTO"
  }

  def regexpFlag(regexpFlag: RegexpFlag): String = regexpFlag match {
    case RegexpFlag.Intersection => "INTERSECTION"
    case RegexpFlag.Complement   => "COMPLEMENT"
    case RegexpFlag.Empty        => "EMPTY"
    case RegexpFlag.AnyString    => "ANYSTRING"
    case RegexpFlag.Interval     => "INTERVAL"
    case RegexpFlag.All          => "ALL"
    case RegexpFlag.None         => "NONE"
  }

  def zeroTermsQuery(terms: ZeroTermsQuery): String = terms match {
    case ZeroTermsQuery.All  => "all"
    case ZeroTermsQuery.None => "none"
  }

  def multiMatchQueryBuilderType(mtype: MultiMatchQueryBuilderType): String = mtype match {
    case BEST_FIELDS   => "best_fields"
    case MOST_FIELDS   => "most_fields"
    case CROSS_FIELDS  => "cross_fields"
    case PHRASE        => "phrase"
    case PHRASE_PREFIX => "phrase_prefix"
  }

  def multiValueMode(mode: MultiValueMode): String =
    mode match {
      case MultiValueMode.Avg    => "avg"
      case MultiValueMode.Max    => "max"
      case MultiValueMode.Min    => "min"
      case MultiValueMode.Sum    => "sum"
      case MultiValueMode.Median => "median"
    }

}
