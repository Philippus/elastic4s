package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class DeleteByQueryTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

    Try {
      http.execute {
        deleteIndex("charlesd")
      }.await
    }

  http.execute {
      createIndex("charlesd").mappings(
        mapping("characters").fields(
          textField("name")
        )
      ).shards(1).waitForActiveShards(1)
    }.await

  "delete by query" should {
    "delete matched docs" in {
      http.execute {
        bulk(
          indexInto("charlesd" / "characters").fields("name" -> "mr bumbles").id(1),
          indexInto("charlesd" / "characters").fields("name" -> "artful dodger").id(2),
          indexInto("charlesd" / "characters").fields("name" -> "mrs bumbles").id(3),
          indexInto("charlesd" / "characters").fields("name" -> "fagan").id(4)
        ).refresh(RefreshPolicy.Immediate)
      }.await

      http.execute {
        search("charlesd" / "characters").matchAllQuery()
      }.await.totalHits shouldBe 4

      http.execute {
        deleteIn("charlesd").by(matchQuery("name", "bumbles")).refresh(RefreshPolicy.Immediate)
      }.await.deleted shouldBe 2

      Thread.sleep(5000)

      http.execute {
        search("charlesd" / "characters").matchAllQuery()
      }.await.totalHits shouldBe 2
    }
  }
}
