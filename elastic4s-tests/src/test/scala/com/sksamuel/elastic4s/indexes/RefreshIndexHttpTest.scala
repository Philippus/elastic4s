package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.duration._

class RefreshIndexHttpTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "refresh index request" should {
    "refresh pending docs" in {

      http.execute {
        createIndex("beaches").mappings(
          mapping("dday").fields(
            textField("name")
          )
        ).shards(1).waitForActiveShards(1).refreshInterval(10.minutes)
      }.await

      http.execute {
        indexInto("beaches" / "dday").fields("name" -> "omaha")
      }.await

      // no data will have been refreshed for 10 minutes
      client.execute {
        search("beaches" / "dday").matchAll()
      }.await.totalHits shouldBe 0

      http.execute {
        refreshIndex("beaches")
      }.await

      client.execute {
        search("beaches" / "dday").matchAll()
      }.await.totalHits shouldBe 1
    }
  }
}
