package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.indexes.RefreshPolicy
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

/** @author Stephen Samuel */
class BulkDslTest extends FlatSpec with Matchers with ElasticSugar {

  "the bulk dsl" should "accept index and delete types" in {
    client.execute {
      bulk(
        index into "knightrider/character" fields "name" -> "michael",
        delete id 12 from "knightrider/vehicle"
      )
    }
  }

  it should "accept refresh property" in {
    client.execute {
      bulk(
        index into "knightrider/character" fields "name" -> "michael",
        delete id 12 from "knightrider/vehicle"
      ).refresh(RefreshPolicy.Immediate)
    }
  }

  it should "accept timeout" in {
    client.execute {
      bulk(
        index into "knightrider/character" fields "name" -> "michael",
        delete id 12 from "knightrider/vehicle"
      ).timeout("123ms")
    }
  }

  it should "accept timeout as duration" in {
    client.execute {
      bulk(
        index into "knightrider/character" fields "name" -> "michael",
        delete id 12 from "knightrider/vehicle"
      ).timeout(123.millis)
    }
  }
}
