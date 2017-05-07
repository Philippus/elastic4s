package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class CatCountTest extends FlatSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    bulk(
      indexInto("catcount1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catcount1/landmarks").fields("name" -> "tower of london"),
      indexInto("catcount2/landmarks").fields("name" -> "stonehenge")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await


  "cats count" should "return count for all cluster" in {
    http.execute {
      catCount()
    }.await.count >= 3 shouldBe true
  }

  it should "support counting for a single index" in {
    http.execute {
      catCount("catcount1")
    }.await.count shouldBe 2
  }

  it should "support counting for multiple indices" in {
    http.execute {
      catCount("catcount1", "catcount2")
    }.await.count shouldBe 3
  }

}
