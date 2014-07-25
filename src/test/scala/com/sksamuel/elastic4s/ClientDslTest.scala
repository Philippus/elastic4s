package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{ GeoPointType, StringType }
import org.scalatest.FunSuite

/** @author Stephen Samuel */
class ClientDslTest extends FunSuite with ElasticSugar {

  client.execute {
    index into "gameofthrones/characters" fields (
      "name" -> "tyrion",
      "rating" -> "kick ass"
    )
  }.await

  refresh("gameofthrones")
  blockUntilCount(1, "gameofthrones")

  test("async compiles with mapping from") {
    client.execute {
      mapping from "gameofthrones"
    }
  }

  test("async accepts update mapping") {
    client.execute {
      put mapping "gameofthrones/places" add (
        "name" typed StringType,
        "location" typed GeoPointType
      )
    }
  }

}
