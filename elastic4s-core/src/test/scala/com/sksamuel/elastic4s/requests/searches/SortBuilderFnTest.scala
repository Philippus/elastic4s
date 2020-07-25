package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.searches.queries.{FieldSortBuilderFn, GeoDistanceSortBuilderFn, RangeQuery}
import com.sksamuel.elastic4s.requests.searches.sort.{FieldSort, GeoDistanceSort}
import com.sksamuel.elastic4s.requests.searches.sort.SortMode.{Avg, Min}
import com.sksamuel.elastic4s.requests.searches.sort.SortOrder.Asc
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SortBuilderFnTest extends AnyFunSuite with Matchers {

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
}
