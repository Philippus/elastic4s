package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class DeleteByQueryTest extends WordSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  "delete by query" should {
    "delete matched docs" in {

      execute {
        createIndex("charlesd").mappings(
          mapping("characters").fields(
            textField("name")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      execute {
        bulk(
          indexInto("charlesd" / "characters").fields("name" -> "mr bumbles").id(1),
          indexInto("charlesd" / "characters").fields("name" -> "artful dodger").id(2),
          indexInto("charlesd" / "characters").fields("name" -> "mrs bumbles").id(3),
          indexInto("charlesd" / "characters").fields("name" -> "fagan").id(4)
        ).refresh(RefreshPolicy.IMMEDIATE)
      }.await

      execute {
        search("charlesd" / "characters").matchAllQuery()
      }.await.totalHits shouldBe 4

      execute {
        deleteIn("charlesd").by(matchQuery("name", "bumbles")).refresh(RefreshPolicy.IMMEDIATE)
      }.await.deleted shouldBe 2

      Thread.sleep(5000)

      execute {
        search("charlesd" / "characters").matchAllQuery()
      }.await.totalHits shouldBe 2
    }
  }
}
