package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.VersionType
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.{DateHistogramInterval, ScoreMode}
import com.sksamuel.elastic4s.searches.aggs.{HistogramOrder, SubAggCollectionMode, TermsOrder}
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

  def order(order: HistogramOrder): String = ???

  def timeZone(zone: DateTimeZone): String = ???

  def interval(interval: DateHistogramInterval): String = ???


  def scoreMode(scoreMode: ScoreMode): String = ???

  def geoExecType(execType: GeoExecType): String = ???

  def geoValidationMethod(method: GeoValidationMethod): String = ???

  def collectMode(mode: SubAggCollectionMode): String = ???

  def versionType(versionType: VersionType): String = ???

  def sortBy(by: SortBy): String = ???

  def suggestMode(mode: SuggestMode): String = ???

  def stringDistance(impl: StringDistanceImpl): String = ???

  def simpleQueryStringFlag(flag: SimpleQueryStringFlag): String = ???

  def zeroTermsQuery(terms: ZeroTermsQuery): String = ???

  def multiMatchQueryBuilderType(mtype: MultiMatchQueryBuilderType): String = ???

}
