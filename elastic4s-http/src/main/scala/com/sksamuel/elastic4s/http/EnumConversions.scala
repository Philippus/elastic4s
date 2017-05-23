package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.VersionType
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.{DateHistogramInterval, ScoreMode}
import com.sksamuel.elastic4s.searches.aggs.{HistogramOrder, SubAggCollectionMode, TermsOrder}
import com.sksamuel.elastic4s.searches.queries.funcscorer.{CombineFunction, FunctionScoreQueryScoreMode}
import com.sksamuel.elastic4s.searches.queries.{RegexpFlag, SimpleQueryStringFlag}
import com.sksamuel.elastic4s.searches.queries.geo.{GeoDistance, GeoExecType, GeoValidationMethod}
import com.sksamuel.elastic4s.searches.queries.matches.{MultiMatchQueryBuilderType, ZeroTermsQuery}
import com.sksamuel.elastic4s.searches.sort.{SortMode, SortOrder}
import com.sksamuel.elastic4s.searches.suggestion.{SortBy, StringDistanceImpl, SuggestMode}
import org.joda.time.DateTimeZone

object EnumConversions {

  def regexflag(flag: RegexpFlag): String = flag.toString.toUpperCase

  def order(order: SortOrder): String = order match {
    case SortOrder.Asc => "asc"
    case SortOrder.Desc => "desc"
  }

  def sortMode(mode: SortMode): String = mode.toString.toLowerCase

  def geoDistance(distance: GeoDistance): String = distance.toString.toLowerCase

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

  def stringDistance(impl: StringDistanceImpl): String = ???

  def simpleQueryStringFlag(flag: SimpleQueryStringFlag): String = ???

  def zeroTermsQuery(terms: ZeroTermsQuery): String = ???

  def multiMatchQueryBuilderType(mtype: MultiMatchQueryBuilderType): String = ???

}
