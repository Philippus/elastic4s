package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{Matchers, WordSpec}

class TypeExistsHttpTest extends WordSpec with SharedElasticSugar with Matchers with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("typeexists").mappings {
      mapping("flowers") fields textField("name")
    }
  }.await

  "a type exists request" should {
    "return true for an existing type" in {
      http.execute {
        typesExist("typeexists" / "flowers")
      }.await.isExists shouldBe true
    }
    "return false for non existing type" in {
      http.execute {
        typesExist("typeexists" / "qeqweqew")
      }.await.isExists shouldBe false
    }
  }
}
