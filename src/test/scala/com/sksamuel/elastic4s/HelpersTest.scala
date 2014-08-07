package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{ FlatSpec, Matchers }

/** @author Stephen Samuel */
class HelpersTest extends FlatSpec with MockitoSugar with ElasticSugar with Matchers {

  client.sync.execute {
    index into "starcraft/races" fields (
      "name" -> "zerg",
      "base" -> "hatchery"
    )
  }

  client.sync.execute {
    index into "starcraft/units" fields (
      "name" -> "hydra",
      "race" -> "zerg"
    )
  }

  client.sync.execute {
    index into "starcraft/bands" fields (
      "name" -> "protoss",
      "base" -> "nexus"
    ) id 45
  }

  blockUntilCount(3, "starcraft")

  "reindex" should "reindex all documents from source to target" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    client.sync.reindex("starcraft", "games")

    blockUntilCount(3, "games")

    val resp = client.sync.execute {
      search in "games" query "protoss"
    }

    resp.getHits.totalHits() shouldBe 1
  }

}
