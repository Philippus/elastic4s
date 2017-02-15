package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{Matchers, WordSpec}

class IndexExistsHttpTest extends WordSpec with SharedElasticSugar with Matchers with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("indexexists").mappings {
      mapping("flowers") fields textField("name")
    }
  }.await

  "an index exists request" should {
    "return true for an existing index" in {
      http.execute {
        indexExists("indexexists")
      }.await.isExists shouldBe true
    }
    "return false for non existing index" in {
      http.execute {
        indexExists("qweqwewqe")
      }.await.isExists shouldBe false
    }
  }
}
