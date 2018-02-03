package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.DistanceUnit
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.search.SearchBodyBuilderFn
import com.sksamuel.elastic4s.script.ScriptType
import com.sksamuel.elastic4s.searches.GeoPoint
import com.sksamuel.elastic4s.searches.queries.geo.GeoDistanceQuery
import com.sksamuel.elastic4s.searches.sort.{GeoDistanceSort, SortOrder, ScriptSortType}
import org.scalatest.{FunSuite, Matchers}
import com.sksamuel.exts.OptionImplicits._

class SortBuilderFnTest extends FunSuite with Matchers {

  test("sort script parameters are encoded with the correct type") {
    val scr = script("dummy script")
      .lang("painless")
      .scriptType(ScriptType.Source)
      .params(Map(
        "nump" -> 10.2,
        "stringp" -> "ciao",
        "boolp" -> true
      ))
    val request = scriptSort(scr)
      .typed(ScriptSortType.Number)
      .order(SortOrder.Desc)
    SortBuilderFn(request).string() shouldBe
      """{"_script":{"script":{"source":"dummy script","lang":"painless","params":{"nump":10.2,"stringp":"ciao","boolp":true}},"type":"number","order":"desc"}}"""
  }
}

