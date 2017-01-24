package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class DeleteByIdHttpTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "delete by id request" should {
    "delete matched docs" in {

      http.execute {
        createIndex("lecarre").mappings(
          mapping("characters").fields(
            textField("name")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      http.execute {
        indexInto("lecarre" / "characters").fields("name" -> "jonathon pine").id(2).refresh(RefreshPolicy.IMMEDIATE)
      }.await

      http.execute {
        indexInto("lecarre" / "characters").fields("name" -> "george smiley").id(4).refresh(RefreshPolicy.IMMEDIATE)
      }.await

      client.execute {
        search("lecarre" / "characters").matchAll()
      }.await.totalHits shouldBe 2

      http.execute {
        delete(2).from("lecarre" / "characters").refresh(RefreshPolicy.IMMEDIATE)
      }.await

      client.execute {
        search("lecarre" / "characters").matchAll()
      }.await.totalHits shouldBe 1

      http.execute {
        delete(4).from("lecarre" / "characters").refresh(RefreshPolicy.IMMEDIATE)
      }.await

      client.execute {
        search("lecarre" / "characters").matchAll()
      }.await.totalHits shouldBe 0
    }
  }
}
