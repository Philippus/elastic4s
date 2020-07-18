package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.script.ScriptType
import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.sort.{GeoDistanceSort, ScriptSortType, SortOrder}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SortBuilderFnTest extends AnyFunSuite with Matchers {

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
      .nestedPath("foo.bar")
      .nestedFilter(matchQuery("foo.bar", "foo"))
    SortBuilderFn(request).string() shouldBe
      """{"_script":{"script":{"source":"dummy script","lang":"painless","params":{"nump":10.2,"stringp":"ciao","boolp":true}},"type":"number","order":"desc","path":"foo.bar","filter":{"match":{"foo.bar":{"query":"foo"}}}}}"""
  }

  test("geo distance sort does not generate unit field by default") {
    val sort = GeoDistanceSort(
      field = "location",
      points = Seq(GeoPoint(43.65435, -79.38871))
    )

    SortBuilderFn(sort).string() shouldBe
      """{"_geo_distance":{"location":[[-79.38871,43.65435]]}}"""
  }

  test("geo distance sort generates unit field when informed") {
    val sort = GeoDistanceSort(
      field = "location",
      points = Seq(GeoPoint(43.65435, -79.38871)),
      unit = Some(DistanceUnit.Kilometers)
    )

    SortBuilderFn(sort).string() shouldBe
      """{"_geo_distance":{"location":[[-79.38871,43.65435]],"unit":"km"}}"""
  }
}

