package com.sksamuel.elastic4s.requests.searches.queries.geo

import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.geo.Shapes.{Circle, Polygon}
import com.sksamuel.elastic4s.requests.searches.queries.geo._
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

  test("Should correctly build empty geo shape geometry collection search query") {
    Given("Some collection shape query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        GeometryCollectionShape(
          Seq()
        )
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have all shapes in collection specified")
    queryBody.string() shouldEqual emptyGeometryCollectionQuery
  }

  test("Should correctly build single level geo shape geometry collection search query") {
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
    queryBody.string() shouldEqual singleLevelGeometryCollectionQuery
  }

  test("Should correctly build multi level geo shape geometry collection search query") {
    Given("Some collection shape query")
    val query = GeoShapeQuery(
      "location",
      InlineShape(
        GeometryCollectionShape(
          Seq(
            CircleShape(Circle(GeoPoint(23.23,100.23),(100.0,DistanceUnit.Meters))),
            PointShape(GeoPoint(23.23,100.23)),
            GeometryCollectionShape(
              Seq(
                CircleShape(Circle(GeoPoint(23.23,200.23),(200.0,DistanceUnit.Meters))),
                PointShape(GeoPoint(23.23,200.23)),
                GeometryCollectionShape(
                  Seq(
                    CircleShape(Circle(GeoPoint(23.23,300.23),(300.0,DistanceUnit.Meters))),
                    PointShape(GeoPoint(23.23,300.23))
                  )
                )
              )
            )
          )
        )
      )
    )

    When("Geo shape query is built")
    val queryBody = GeoShapeQueryBodyFn(query)

    Then("query should have all shapes in collection specified")
    queryBody.string() shouldEqual multiLevelGeometryCollectionQuery
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
              Seq(
                GeoPoint(100.2, 0.2),
                GeoPoint(100.4, 0.2),
                GeoPoint(100.4, 0.4),
                GeoPoint(100.2, 0.4),
                GeoPoint(100.2, 0.2)
              ),
              Seq(
                GeoPoint(100.6, 0.6),
                GeoPoint(100.8, 0.6),
                GeoPoint(100.8, 0.8),
                GeoPoint(100.6, 0.8),
                GeoPoint(100.6, 0.6)
              )
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
                  Seq(
                    GeoPoint(100.2, 0.2),
                    GeoPoint(100.4, 0.2),
                    GeoPoint(100.4, 0.4),
                    GeoPoint(100.2, 0.4),
                    GeoPoint(100.2, 0.2)
                  ),
                  Seq(
                    GeoPoint(100.6, 0.6),
                    GeoPoint(100.8, 0.6),
                    GeoPoint(100.8, 0.8),
                    GeoPoint(100.6, 0.8),
                    GeoPoint(100.6, 0.6)
                  )
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

  def polygonQuery: String =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"polygon",
    |            "coordinates":[
    |               [[0.0,100.0],[0.0,101.0],[1.0,101.0],[1.0,100.0],[0.0,100.0]],
    |               [[0.2,100.2],[0.2,100.4],[0.4,100.4],[0.4,100.2],[0.2,100.2]],
    |               [[0.6,100.6],[0.6,100.8],[0.8,100.8],[0.8,100.6],[0.6,100.6]]
    |            ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def multiPolygonQuery: String =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"multipolygon",
    |            "coordinates":[
    |               [ [[2.0,102.0],[2.0,103.0],[3.0,103.0],[3.0,102.0],[2.0,102.0]] ],
    |               [ [[0.0,100.0],[0.0,101.0],[1.0,101.0],[1.0,100.0],[0.0,100.0]],
    |                 [[0.2,100.2],[0.2,100.4],[0.4,100.4],[0.4,100.2],[0.2,100.2]],
    |                 [[0.6,100.6],[0.6,100.8],[0.8,100.8],[0.8,100.6],[0.6,100.6]] ]
    |            ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def pointQuery: String =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"point",
    |            "coordinates":[38.897676,-77.03653]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def pointQueryWithRelation: String =
    """
      |{
      |   "geo_shape":{
      |      "location":{
      |         "shape":{
      |            "type":"point",
      |            "coordinates":[38.897676,-77.03653]
      |         },
      |         "relation": "within"
      |      }
      |   }
      |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def envelopeQuery: String =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"envelope",
    |            "coordinates":[ [45.0,-45.0],[-45.0,45.0] ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def multiPointQuery: String =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"multipoint",
    |            "coordinates":[ [2.0,102.0],[3.0,102.0] ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def lineStringQuery: String =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"linestring",
    |            "coordinates":[ [38.897676,-77.03653],[38.889939,-77.009051] ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def multiLineStringQuery: String =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"multilinestring",
    |            "coordinates":[
    |               [ [2.0,102.0],[2.0,103.0],[3.0,103.0],[3.0,102.0] ],
    |               [ [0.0,100.0],[0.0,101.0],[1.0,101.0],[1.0,100.0] ],
    |               [ [0.2,100.2],[0.2,100.8],[0.8,100.8],[0.8,100.2] ]
    |            ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def circleQuery: String =
  """|
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"circle",
    |            "coordinates":[100.23,23.23],
    |            "radius":"100.0m"
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def singleLevelGeometryCollectionQuery: String =
  """
    |{
    |   "geo_shape":{
    |      "location":{
    |         "shape":{
    |            "type":"geometrycollection",
    |            "geometries":[
    |               {
    |                  "type":"circle",
    |                  "coordinates":[100.23,23.23],
    |                  "radius":"100.0m"
    |               },
    |               {
    |                  "type":"point",
    |                  "coordinates":[100.23,23.23]
    |               }
    |            ]
    |         }
    |      }
    |   }
    |}
  """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def multiLevelGeometryCollectionQuery: String =
    """
      |{
      |   "geo_shape":{
      |      "location":{
      |         "shape":{
      |            "type":"geometrycollection",
      |            "geometries":[
      |               {
      |                  "type":"circle",
      |                  "coordinates":[100.23,23.23],
      |                  "radius":"100.0m"
      |               },
      |               {
      |                  "type":"point",
      |                  "coordinates":[100.23,23.23]
      |               },
      |               {
      |                  "type":"geometrycollection",
      |                  "geometries":[
      |                     {
      |                        "type":"circle",
      |                        "coordinates":[200.23,23.23],
      |                        "radius":"200.0m"
      |                     },
      |                     {
      |                        "type":"point",
      |                        "coordinates":[200.23,23.23]
      |                     },
      |                     {
      |                        "type":"geometrycollection",
      |                        "geometries":[
      |                           {
      |                              "type":"circle",
      |                              "coordinates":[300.23,23.23],
      |                              "radius":"300.0m"
      |                           },
      |                           {
      |                              "type":"point",
      |                              "coordinates":[300.23,23.23]
      |                           }
      |                        ]
      |                     }
      |                  ]
      |               }
      |            ]
      |         }
      |      }
      |   }
      |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  def emptyGeometryCollectionQuery: String =
    """
      |{
      |   "geo_shape":{
      |      "location":{
      |         "shape":{
      |            "type":"geometrycollection",
      |            "geometries":[]
      |         }
      |      }
      |   }
      |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")
}


