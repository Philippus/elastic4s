package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.mappings.FieldType.{StringType, GeoPointType, DateType}
import org.elasticsearch.common.Priority
import org.scalatest.FunSuite
import ElasticDsl._

/** @author Stephen Samuel */
class ClientDslTest extends FunSuite with ElasticSugar {

  client.execute {
    index into "gameofthrones/characters" fields(
      "name" -> "tyrion",
      "rating" -> "kick ass"
      )
  }

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("gameofthrones")
  blockUntilCount(1, "gameofthrones")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  test("sync compiles with mapping from") {
    client.execute {
      mapping from "gameofthrones"
    }
  }

  test("async compiles with mapping from") {
    client.sync.execute {
      mapping from "gameofthrones"
    }
  }

  test("async accepts update mapping") {
    client.execute {
      put mapping "gameofthrones/places" add(
        "name" typed StringType,
        "location" typed GeoPointType
        )
    }
  }

  test("sync accepts update mapping") {
    client.sync.execute {
      put mapping "gameofthrones/places" add(
        "name" typed StringType,
        "location" typed GeoPointType
        )
    }
  }
}
