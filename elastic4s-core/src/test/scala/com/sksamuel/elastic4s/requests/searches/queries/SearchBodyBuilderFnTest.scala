package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.searches.queries.geo.GeoDistanceQuery
import com.sksamuel.elastic4s.requests.searches.sort.{GeoDistanceSort, SortOrder}
import com.sksamuel.elastic4s.requests.searches.{GeoPoint, SearchBodyBuilderFn}
import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.searches.knn.Knn
import com.sksamuel.elastic4s.requests.searches.term.TermQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SearchBodyBuilderFnTest extends AnyFunSuite with Matchers {

  test("highlight with 'matchedMatchedFields' generates proper 'matched_fields' field as array field.") {
    val request = search("example") highlighting {
      highlight("text")
      .matchedFields("text", "text.ngram", "text.japanese")
    }
    SearchBodyBuilderFn(request).string shouldBe
      """{"highlight":{"fields":{"text":{"matched_fields":["text","text.ngram","text.japanese"]}}}}"""
  }
  test("highlight with 'highlighterType' generates 'type' field.") {
    val request = search("example") highlighting {
      highlight("text")
        .highlighterType("fvh")
    }
    SearchBodyBuilderFn(request).string shouldBe
      """{"highlight":{"fields":{"text":{"type":"fvh"}}}}"""
  }
  test("highlight with 'boundaryChars' generates 'boundary_chars' field.") {
    val request = search("example") highlighting {
      highlight("text")
        .boundaryChars("test")
    }
    SearchBodyBuilderFn(request).string shouldBe
      """{"highlight":{"fields":{"text":{"boundary_chars":"test"}}}}"""
  }
  test("geo distance query with sort") {

    val geoDistanceQueryDefinition = GeoDistanceQuery(
      field = "location",
      point = Some(43.65435, -79.38871),
      distanceStr = "100km".some
    )

    val req = search("partner-location") limit 100 query geoDistanceQueryDefinition sortBy GeoDistanceSort(
      field = "location",
      points = Seq(GeoPoint(43.65435, -79.38871)),
      order = Some(SortOrder.ASC),
      unit = Some(DistanceUnit.KILOMETERS)
    )

    SearchBodyBuilderFn(req).string shouldBe
      """{"query":{"geo_distance":{"distance":"100km","location":[-79.38871,43.65435]}},"size":100,"sort":[{"_geo_distance":{"location":[[-79.38871,43.65435]],"order":"asc","unit":"km"}}]}"""
  }

  test("multiple Knn queries") {
    val multipleKnnDefinition =Seq(
      Knn("image-vector", 50, Seq(54.1, 10.2, -2.3)),
      Knn("image-vector", 50, Seq(54.1, 10.2, -2.3)) k 5 filter TermQuery("file-type", "png") similarity 10 boost .4)

    val req = search("example") multipleKnn multipleKnnDefinition

    SearchBodyBuilderFn(req).string shouldBe
      """{"knn":[{"field":"image-vector","query_vector":[54.1,10.2,-2.3],"k":1,"num_candidates":50,"boost":1.0},{"field":"image-vector","query_vector":[54.1,10.2,-2.3],"k":5,"num_candidates":50,"similarity":10.0,"filter":{"term":{"file-type":{"value":"png"}}},"boost":0.4}]}"""
  }
}
