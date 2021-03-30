package com.sksamuel.elastic4s.handlers.searches.queries.sort

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.sort.{FieldSort, GeoDistanceSort, NestedSort, ScoreSort, ScriptSort, Sort}

object SortBuilderFn {
  def apply(sort: Sort): XContentBuilder = sort match {
    case fs: FieldSort => FieldSortBuilderFn(fs)
    case gs: GeoDistanceSort => GeoDistanceSortBuilderFn(gs)
    case ss: ScoreSort => ScoreSortBuilderFn(ss)
    case scrs: ScriptSort => ScriptSortBuilderFn(scrs)
  }
}










