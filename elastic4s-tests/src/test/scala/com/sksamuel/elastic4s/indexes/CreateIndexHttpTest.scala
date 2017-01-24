package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.{AbstractElasticSugar, ClassloaderLocalNodeProvider, SharedElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class CreateIndexHttpTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "CreateIndex Http Request" should {
    "return ack" in {
      val resp = http.execute {
        createIndex("cusine").mappings(
          mapping("food").fields(
            textField("name"),
            geopointField("location")
          )
        ).shards(1).waitForActiveShards(1)
      }.await
      resp.acknowledged shouldBe true
    }
    "support multiple types" in {

      http.execute {
        createIndex("geography").mappings(
          mapping("shire").fields(
            textField("name")
          ),
          mapping("mountain").fields(
            textField("range")
          )
        ).shards(1).waitForActiveShards(1)
      }.await

      val resp = client.execute {
        getMapping("geography").types("shire", "mountain")
      }.await

      resp.mappings.keys shouldBe Set("geography")
      resp.mappings("geography").keySet shouldBe Set("shire", "mountain")
    }
  }
}
