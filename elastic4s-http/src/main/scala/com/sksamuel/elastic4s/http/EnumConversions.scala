package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.VersionType
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.QueryRescoreMode.{Avg, Max, Min, Multiply, Total}
import com.sksamuel.elastic4s.searches.aggs.{HistogramOrder, SubAggCollectionMode, TermsOrder}
import com.sksamuel.elastic4s.searches.queries.funcscorer.{CombineFunction, FunctionScoreQueryScoreMode, MultiValueMode}
import com.sksamuel.elastic4s.searches.queries.geo.GeoDistance.{Arc, Plane}
import com.sksamuel.elastic4s.searches.queries.geo.{GeoDistance, GeoExecType, GeoValidationMethod}
import com.sksamuel.elastic4s.searches.queries.matches.MultiMatchQueryBuilderType.{BEST_FIELDS, CROSS_FIELDS, MOST_FIELDS, PHRASE, PHRASE_PREFIX}
import com.sksamuel.elastic4s.searches.queries.matches.{MultiMatchQueryBuilderType, ZeroTermsQuery}
import com.sksamuel.elastic4s.searches.queries.{RegexpFlag, SimpleQueryStringFlag}
import com.sksamuel.elastic4s.searches.sort.{SortMode, SortOrder}
import com.sksamuel.elastic4s.searches.suggestion.{Fuzziness, SortBy, StringDistanceImpl, SuggestMode}
import com.sksamuel.elastic4s.searches.{DateHistogramInterval, QueryRescoreMode, ScoreMode}
import org.joda.time.DateTimeZone

object EnumConversions {

  def regexflag(flag: RegexpFlag): String = flag.toString.toUpperCase

  def order(order: SortOrder): String = order match {
    case SortOrder.Asc => "asc"
    case SortOrder.Desc => "desc"
  }

  def queryRescoreMode(mode: QueryRescoreMode): String = mode match {
    case Avg => "avg"
    case Max => "max"
    case Min => "min"
    case Total => "total"
    case Multiply => "multiply"
  }

  def sortMode(mode: SortMode): String = mode.toString.toLowerCase

  def geoDistance(distance: GeoDistance): String = distance match {
    case Arc => "arc"
    case Plane => "plane"
  }

  def order(order: TermsOrder): XContentBuilder = {
    val builder = XContentFactory.obj()
    if (order.asc) {
      builder.field(order.name, "asc")
    } else {
      builder.field(order.name, "desc")
    }
    builder.endObject()
  }

  def order(order: HistogramOrder): XContentBuilder = {
    val builder = XContentFactory.obj()
    if (order.asc) {
      builder.field(order.name, "asc")
    } else {
      builder.field(order.name, "desc")
    }
    builder.endObject()
  }

  def timeZone(zone: DateTimeZone): String = ???

  def interval(interval: DateHistogramInterval): String = interval.interval

  def scoreMode(scoreMode: ScoreMode): String = scoreMode.toString.toLowerCase

  def scoreMode(scoreMode: FunctionScoreQueryScoreMode): String = scoreMode.toString.toLowerCase

  def boostMode(combineFunction: CombineFunction): String = combineFunction.toString.toLowerCase

  def geoExecType(execType: GeoExecType): String = execType.toString.toLowerCase

  def geoValidationMethod(method: GeoValidationMethod): String = method match {
    case GeoValidationMethod.Coerce => "COERCE"
    case GeoValidationMethod.IgnoreMalformed => "IGNORE_MALFORMED"
    case GeoValidationMethod.Strict => "STRICT"
  }

  def collectMode(mode: SubAggCollectionMode): String = ???

  def versionType(versionType: VersionType): String = versionType match {
    case VersionType.External => "external"
    case VersionType.ExternalGte => "external_gte"
    case VersionType.Force => "force"
    case VersionType.Internal => "internal"
  }

  def sortBy(by: SortBy): String = by.toString.toLowerCase

  def suggestMode(mode: SuggestMode): String = mode.toString.toLowerCase

  def stringDistance(impl: StringDistanceImpl): String = impl.toString

  def simpleQueryStringFlag(flag: SimpleQueryStringFlag): String = flag.toString.toUpperCase

  def fuzziness(fuzziness: Fuzziness): String = fuzziness.toString

  def zeroTermsQuery(terms: ZeroTermsQuery): String = terms match {
    case ZeroTermsQuery.All => "all"
    case ZeroTermsQuery.None => "none"
  }

  def multiMatchQueryBuilderType(mtype: MultiMatchQueryBuilderType): String = mtype match {
    case BEST_FIELDS => "best_fields"
    case MOST_FIELDS => "most_fields"
    case CROSS_FIELDS => "cross_fields"
    case PHRASE => "phrase"
    case PHRASE_PREFIX => "phrase_prefix"
  }

  def multiValueMode(mode: MultiValueMode): String = {
    mode match {
      case MultiValueMode.Avg => "avg"
      case MultiValueMode.Max => "max"
      case MultiValueMode.Min => "min"
      case MultiValueMode.Sum => "sum"
      case MultiValueMode.Median => "median"
    }
  }

}
