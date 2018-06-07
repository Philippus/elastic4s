package com.sksamuel.elastic4s.http.search.queries.geo

import com.sksamuel.elastic4s.DistanceUnit
import com.sksamuel.elastic4s.searches.GeoPoint
import com.sksamuel.elastic4s.searches.queries.geo.Shapes.{Circle, Polygon}
import com.sksamuel.elastic4s.searches.queries.geo._
import org.scalatest.{FunSuite, GivenWhenThen, Matchers}

class GeoShapeQueryBodyFnTest extends FunSuite with Matchers with GivenWhenThen {

  test("Should correctly build geo shape point search query") {
    Given("Some point query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        PointShape(GeoPoint(-77.03653, 38.897676))
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have right field and coordinate")
    queryBody.string() shouldEqual pointQuery
  }

  test("Should correctly build geo shape point with relation search query") {
    Given("Some point query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        PointShape(GeoPoint(-77.03653, 38.897676))
      )
    ).relation(ShapeRelation.within)

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have right field and coordinate")
    queryBody.string() shouldEqual pointQueryWithRelation
  }

  test("Should correctly build geo shape envelope query") {
    Given("Some envelope query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        EnvelopeShape(
          upperLeft = GeoPoint(-45.0, 45.0),
          lowerRight = GeoPoint(45.0, -45.0)
        )
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have right field and coordinates")
    queryBody.string() shouldEqual envelopeQuery
  }

  test("Should correctly build geo shape multipoint query") {
    Given("Some multipoint query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        MultiPointShape(Seq(GeoPoint(102.0,2.0),GeoPoint(102.0,3.0)))
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have right field and coordinates")
    queryBody.string() shouldEqual multiPointQuery
  }

  test("Should correctly build geo shape linestring query") {
    Given("Some linestring query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        LineStringShape(GeoPoint(-77.03653, 38.897676),GeoPoint(-77.009051, 38.889939))
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have right field and coordinates")
    queryBody.string() shouldEqual lineStringQuery

  }

  test("Should correctly build geo shape multilinestring query") {
    Given("Some multi linestring query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        MultiLineStringShape(Seq(
          Seq(GeoPoint(102.0, 2.0), GeoPoint(103.0, 2.0), GeoPoint(103.0, 3.0), GeoPoint(102.0, 3.0)),
          Seq(GeoPoint(100.0, 0.0), GeoPoint(101.0, 0.0), GeoPoint(101.0, 1.0), GeoPoint(100.0, 1.0)),
          Seq(GeoPoint(100.2, 0.2), GeoPoint(100.8, 0.2), GeoPoint(100.8, 0.8), GeoPoint(100.2, 0.8))
        ))
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have right field and coordinates")
    queryBody.string() shouldEqual multiLineStringQuery
  }

  test("Should correctly build geo shape circle search query") {
    Given("Some circle query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        CircleShape(Circle(GeoPoint(23.23,100.23),(100.0,DistanceUnit.Meters)))
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have right field, coordinates and radius")
    queryBody.string() shouldEqual circleQuery
  }

  test("Should correctly build geo shape geometry collection search query") {
    Given("Some collection shape query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        GeometryCollectionShape(
          Seq(
            CircleShape(Circle(GeoPoint(23.23,100.23),(100.0,DistanceUnit.Meters))),
            PointShape(GeoPoint(23.23,100.23))
          )
        )
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have all shapes in collection specified")
    queryBody.string() shouldEqual geometryCollectionQuery
  }

  test("Should correctly build geo shape polygon search query") {
    Given("Some polygon shape query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        PolygonShape(Polygon(
          points = Seq(
            GeoPoint(100.0, 0.0),
            GeoPoint(101.0, 0.0),
            GeoPoint(101.0, 1.0),
            GeoPoint(100.0, 1.0),
            GeoPoint(100.0, 0.0)
          ),
          holes = Some(
            Seq(
              GeoPoint(100.2, 0.2),
              GeoPoint(100.8, 0.2),
              GeoPoint(100.8, 0.8),
              GeoPoint(100.2, 0.8),
              GeoPoint(100.2, 0.2)
            )
          ))
        )
      )
    )


    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have right field and coordinates")
    queryBody.string() shouldEqual polygonQuery
  }

  test("Should correctly build geo shape multipolygon search query") {
    Given("Some multipolygon shape query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        MultiPolygonShape(
          Seq(
            Polygon(
              points = Seq(
                GeoPoint(102.0, 2.0),
                GeoPoint(103.0, 2.0),
                GeoPoint(103.0, 3.0),
                GeoPoint(102.0, 3.0),
                GeoPoint(102.0, 2.0)
              ),
              holes = None
            ),
            Polygon(
              points = Seq(
                GeoPoint(100.0, 0.0),
                GeoPoint(101.0, 0.0),
                GeoPoint(101.0, 1.0),
                GeoPoint(100.0, 1.0),
                GeoPoint(100.0, 0.0)
              ),
              holes = Some(
                Seq(
                  GeoPoint(100.2, 0.2),
                  GeoPoint(100.8, 0.2),
                  GeoPoint(100.8, 0.8),
                  GeoPoint(100.2, 0.8),
                  GeoPoint(100.2, 0.2)
                )
              )
            )
          )
        )
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have right field and coordinates")
    queryBody.string() shouldEqual multiPolygonQuery
  }

  def polygonQuery =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"polygon",
    |            "coordinates":[
    |               [[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]],
    |               [[100.2,0.2],[100.8,0.2],[100.8,0.8],[100.2,0.8],[100.2,0.2]]
    |            ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def multiPolygonQuery =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"multipolygon",
    |            "coordinates":[
    |               [ [[102.0,2.0],[103.0,2.0],[103.0,3.0],[102.0,3.0],[102.0,2.0]] ],
    |               [ [[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]],
    |                 [[100.2,0.2],[100.8,0.2],[100.8,0.8],[100.2,0.8],[100.2,0.2]] ]
    |            ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def pointQuery =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"point",
    |            "coordinates":[-77.03653, 38.897676]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def pointQueryWithRelation =
    """
      |{
      |   "geo_shape":{
      |      "location":{
      |         "shape":{
      |            "type":"point",
      |            "coordinates":[-77.03653, 38.897676]
      |         },
      |         "relation": "within"
      |      }
      |   }
      |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def envelopeQuery =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"envelope",
    |            "coordinates":[ [-45.0,45.0],[45.0,-45.0] ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def multiPointQuery =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"multipoint",
    |            "coordinates":[ [102.0,2.0],[102.0,3.0] ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def lineStringQuery =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"linestring",
    |            "coordinates":[ [-77.03653,38.897676],[-77.009051,38.889939] ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def multiLineStringQuery =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"multilinestring",
    |            "coordinates":[
    |               [ [102.0,2.0],[103.0,2.0],[103.0,3.0],[102.0,3.0] ],
    |               [ [100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0] ],
    |               [ [100.2,0.2],[100.8,0.2],[100.8,0.8],[100.2,0.8] ]
    |            ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def circleQuery =
  """|
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"circle",
    |            "coordinates":[23.23,100.23],
    |            "radius":"100.0m"
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def geometryCollectionQuery =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"geometrycollection",
    |            "geometries":[
    |               {
    |                  "type":"circle",
    |                  "coordinates":[23.23,100.23],
    |                  "radius":"100.0m"
    |               },
    |               {
    |                  "type":"point",
    |                  "coordinates":[23.23,100.23]
    |               }
    |            ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")
}


