package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

class BulkHttpTest extends FlatSpec with Matchers with ScalaFutures with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("chemistry").mappings {
      mapping("elements").fields(
        intField("atomicweight").stored(true),
        textField("name").stored(true)
      )
    }
  }.await

  "bulk request" should "handle multiple index operations" in {

    http.execute {
      bulk(
        indexInto("chemistry/elements") fields("atomicweight" -> 2, "name" -> "helium") id 2,
        indexInto("chemistry/elements") fields("atomicweight" -> 4, "name" -> "lithium") id 4
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await.errors shouldBe false

    http.execute {
      get(2).from("chemistry/elements")
    }.await.found shouldBe true

    http.execute {
      get(4).from("chemistry/elements")
    }.await.found shouldBe true
  }
}
