package com.sksamuel.elastic4s.requests.searches.queries.geo

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GeoBoundingBoxQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("it should process geohash field") {
    val query = geoBoxQuery("location").withGeohash("a", "b")
    GeoBoundingBoxQueryBodyFn(query).string() shouldBe
      """{"geo_bounding_box":{"location":{"top_left":"a","bottom_right":"b"}}}"""
  }
}
