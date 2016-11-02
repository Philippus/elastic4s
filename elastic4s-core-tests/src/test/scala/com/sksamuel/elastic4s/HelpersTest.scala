package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.{ElasticMatchers, ElasticSugar}
import org.scalatest.FlatSpec

/** @author Stephen Samuel */
class HelpersTest extends FlatSpec with ElasticSugar with ElasticMatchers {

  client.execute {
    bulk(
      index into "starcraft/races" fields("name" -> "zerg", "base" -> "hatchery"),
      index into "starcraft/units" fields("name" -> "hydra", "race" -> "zerg"),
      index into "starcraft/bands" fields("name" -> "protoss", "base" -> "nexus") id 45
    )
  }.await

  blockUntilCount(3, "starcraft")

  "reindex" should "reindex all documents from source to target" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    client.execute {
      reindex("starcraft", "games")
    }.await

    blockUntilCount(3, "games")
    searches in "games" query "protoss" should haveTotalHits(1)
  }
}
