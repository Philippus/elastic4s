package com.sksamuel.elastic4s.http.search.queries.geo

import com.sksamuel.elastic4s.searches.GeoPoint
import com.sksamuel.elastic4s.searches.queries.geo.{Corners, GeoBoundingBoxQuery}
import org.scalatest.{FunSuite, GivenWhenThen, Matchers}

class GeoBoundingBoxQueryBodyFnTest extends FunSuite with Matchers with GivenWhenThen {

  test("Should correctly build geo box search with top_left and bottom_right corners") {
    Given("Some geo bounding box query")
    val geoQuery = GeoBoundingBoxQuery("locationField")
      .withCorners(
        topLeft = GeoPoint(1.1, 2.2),
        bottomRight = GeoPoint(3.3, 4.4)
      )

    When("Geo bounding box query is built")
    val queryBody = GeoBoundingBoxQueryBodyFn(geoQuery)

    Then("Should have right field and all corners specified")
    queryBody.string() shouldEqual buildBasicGeoBoxQuery()

  }

  test("Should correctly build geo box search with corners set") {
    Given("Some geo bounding box query")
    val geoQuery = GeoBoundingBoxQuery("locationField")
      .withCorners(Corners(
        top = 1.1,
        bottom = 3.3,
        left = 2.2,
        right = 4.4
      ))

    When("Geo bounding box query is built")
    val queryBody = GeoBoundingBoxQueryBodyFn(geoQuery)

    Then("Should have right field and all corners specified")
    queryBody.string() shouldEqual buildBasicGeoBoxQuery()
  }

  test("Should correctly build geo box search with corners") {
    Given("Some geo bounding box query")
    val geoQuery = GeoBoundingBoxQuery("locationField")
      .withCorners(
        top = 1.1,
        bottom = 3.3,
        left = 2.2,
        right = 4.4
      )

    When("Geo bounding box query is built")
    val queryBody = GeoBoundingBoxQueryBodyFn(geoQuery)

    Then("Should have right field and all corners specified")
    queryBody.string() shouldEqual buildBasicGeoBoxQuery()
  }

  def buildBasicGeoBoxQuery() =
    """
      |{
      |  "geo_bounding_box": {
      |    "locationField": {
      |      "top_left": {
      |        "lat": 1.1,
      |        "lon": 2.2
      |      },
      |      "bottom_right": {
      |        "lat": 3.3,
      |        "lon": 4.4
      |      }
      |    }
      |  }
      |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")
}
