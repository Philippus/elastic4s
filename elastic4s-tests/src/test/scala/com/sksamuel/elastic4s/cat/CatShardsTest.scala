package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class CatShardsTest extends FlatSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    bulk(
      indexInto("catshards1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catshards1/landmarks").fields("name" -> "stonehenge"),
      indexInto("catshards1/landmarks").fields("name" -> "kensington palace"),
      indexInto("catshards2/landmarks").fields("name" -> "blenheim palace"),
      indexInto("catshards2/landmarks").fields("name" -> "london eye"),
      indexInto("catshards2/landmarks").fields("name" -> "tower of london")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "cats shards" should "return all shards" in {
    val result = http.execute {
      catShards()
    }.await
    result.map(_.state).toSet shouldBe Set("STARTED", "UNASSIGNED")
    result.map(_.index).contains("catshards1") shouldBe true
    result.map(_.index).contains("catshards2") shouldBe true
  }
}
