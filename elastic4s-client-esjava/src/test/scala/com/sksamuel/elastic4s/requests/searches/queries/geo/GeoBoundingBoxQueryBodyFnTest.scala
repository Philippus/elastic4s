package com.sksamuel.elastic4s.requests.searches.queries.geo

import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.geo.{Corners, GeoBoundingBoxQuery, GeoExecType}
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

  test("Should correctly handle `type`") {
    Given("some geo bouding box query with type specified")
    val geoQuery = GeoBoundingBoxQuery("locationField")
      .withCorners(
        top = 1.1,
        bottom = 3.3,
        left = 2.2,
        right = 4.4
      ).withType(GeoExecType.Memory)

    When("Geo bounding box query is built")
    val queryBody = GeoBoundingBoxQueryBodyFn(geoQuery)

    Then("Should have right field and all corners specified")
    queryBody.string() shouldEqual """
                                     |{
                                     |  "geo_bounding_box": {
                                     |    "type": "memory",
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
