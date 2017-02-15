package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{Matchers, WordSpec}

class ClearCacheHttpTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("clearcache1").mappings(
      mapping("flowers").fields(
        textField("name")
      )
    )
  }.await

  http.execute {
    createIndex("clearcache2").mappings(
      mapping("plants").fields(
        textField("name")
      )
    )
  }.await

  "ClearCache" should {
    "support single index" in {
      val resp = http.execute {
        clearCache("clearcache1")
      }.await
      resp.shards.successful > 0
    }

    "support multiple types" in {
      val resp = http.execute {
        clearCache("clearcache1", "clearcache2")
      }.await
      resp.shards.successful > 0
    }
  }
}
