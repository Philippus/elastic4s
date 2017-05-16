package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.VersionType
import com.sksamuel.elastic4s.searches.ScoreMode
import com.sksamuel.elastic4s.searches.aggs.SubAggCollectionMode
import com.sksamuel.elastic4s.searches.queries.SimpleQueryStringFlag
import com.sksamuel.elastic4s.searches.queries.geo.{GeoExecType, GeoValidationMethod}
import com.sksamuel.elastic4s.searches.queries.matches.{MultiMatchQueryBuilderType, ZeroTermsQuery}
import com.sksamuel.elastic4s.searches.suggestion.{SortBy, StringDistanceImpl, SuggestMode}

object EnumConversions {
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
