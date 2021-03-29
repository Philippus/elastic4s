package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.ElasticDsl.{matchQuery, nestedSort, script, scriptSort}
import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.script.ScriptType
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchQuery
import com.sksamuel.elastic4s.requests.searches.queries.{FieldSortBuilderFn, GeoDistanceSortBuilderFn, RangeQuery, SortBuilderFn}
import com.sksamuel.elastic4s.requests.searches.sort.{FieldSort, GeoDistanceSort, NestedSort, ScriptSortType, SortOrder}
import com.sksamuel.elastic4s.requests.searches.sort.SortMode.Min
import com.sksamuel.elastic4s.requests.searches.sort.SortOrder.Asc
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SortBuilderFnTest extends AnyFunSuite with Matchers with JsonSugar {

  test("field sort builder should support defining both nested path and nested filter") {
    val fieldSort = FieldSort(
      field = "parent.child.age",
      sortMode = Some(Min),
      order = Asc,
      nestedPath = Some("parent"),
      nestedFilter = Some(RangeQuery(field = "parent.child", gte = Some(21L)))
    )

    FieldSortBuilderFn(fieldSort).string() shouldBe
      """{"parent.child.age":{"mode":"min","order":"asc","nested":{"path":"parent","filter":{"range":{"parent.child":{"gte":21}}}}}}"""
  }

  test("field sort builder should correctly build nested option") {
    val fieldSort = FieldSort("parent.child.age").order(Asc).mode(Min).nested(
      NestedSort(
        path = Some("parent"),
        filter = Some(RangeQuery("parent.age", gte = Some(21L))),
        nested = Some(NestedSort(
          path = Some("parent.child"),
          filter = Some(MatchQuery("parent.child.name", "matt"))))))

    FieldSortBuilderFn(fieldSort).string() should matchJson(
      """{
        |         "parent.child.age" : {
        |            "mode" :  "min",
        |            "order" : "asc",
        |            "nested": {
        |               "path": "parent",
        |               "filter": {
        |                  "range": {"parent.age": {"gte": 21}}
        |               },
        |               "nested": {
        |                  "path": "parent.child",
        |                  "filter": {
        |                     "match": {
        |                        "parent.child.name": {
        |                           "query": "matt"
        |                        }
        |                     }
        |                  }
        |               }
        |            }
        |         }
        |     }""".stripMargin)
  }

  test("field sort builder should support numeric_type option") {
    val fieldSort = FieldSort("field").numericType("double")

    FieldSortBuilderFn(fieldSort).string() shouldBe
      """{"field":{"order":"asc","numeric_type":"double"}}""".stripMargin
  }

  test("geo distance sort builder should support ignore_unmapped option") {
    val geoDistanceSort = GeoDistanceSort(field = "pin.location", points = Seq(GeoPoint(40D, -70D)))
      .ignoreUnmapped(true)

    GeoDistanceSortBuilderFn(geoDistanceSort).string() shouldBe
      """{"_geo_distance":{"pin.location":[[-70.0,40.0]],"ignore_unmapped":true}}"""
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
      .nested(nestedSort().path("foo.bar").filter(matchQuery("foo.bar", "foo")))
    SortBuilderFn(request).string() shouldBe
      """{"_script":{"script":{"source":"dummy script","lang":"painless","params":{"nump":10.2,"stringp":"ciao","boolp":true}},"type":"number","order":"desc","nested":{"path":"foo.bar","filter":{"match":{"foo.bar":{"query":"foo"}}}}}}"""
  }
}
