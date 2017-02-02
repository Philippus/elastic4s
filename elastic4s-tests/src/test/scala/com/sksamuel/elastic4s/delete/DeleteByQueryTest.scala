package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class DeleteByQueryTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "delete by query" should {
    "delete matched docs" in {

      http.execute {
        createIndex("charlesd").mappings(
          mapping("characters").fields(
            textField("name")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      http.execute {
        bulk(
          indexInto("charlesd" / "characters").fields("name" -> "mr bumbles").id(1),
          indexInto("charlesd" / "characters").fields("name" -> "artful dodger").id(2),
          indexInto("charlesd" / "characters").fields("name" -> "mrs bumbles").id(3),
          indexInto("charlesd" / "characters").fields("name" -> "fagan").id(4)
        ).refresh(RefreshPolicy.IMMEDIATE)
      }.await

      http.execute {
        search("charlesd" / "characters").matchAll()
      }.await.totalHits shouldBe 4

      http.execute {
        deleteIn("charlesd").by(matchQuery("name", "bumbles")).refresh(RefreshPolicy.IMMEDIATE)
      }.await.deleted shouldBe 2

      Thread.sleep(5000)

      http.execute {
        search("charlesd" / "characters").matchAll()
      }.await.totalHits shouldBe 2
    }
  }
}
