package com.sksamuel.elastic4s.search.collapse

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{BeforeAndAfterAll, FreeSpec, Matchers}

class CollapseHttpTest extends FreeSpec with Matchers with SharedElasticSugar with ElasticDsl with BeforeAndAfterAll {

  var http: HttpClient = _

  override protected def beforeAll(): Unit = {
    http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

    http.execute {
      createIndex("collapse") mappings {
        mapping("hotels") fields(
          keywordField("name") docValues true,
          keywordField("board") docValues true
        )
      }
    }.await

    http.execute {
      bulk(
        indexInto("collapse" / "hotels") id "1" fields("name" -> "Ibiza Playa", "board" -> "AI", "price" -> 150),
        indexInto("collapse" / "hotels") id "2" fields("name" -> "Ibiza Playa", "board" -> "BB", "price" -> 120),

        indexInto("collapse" / "hotels") id "3" fields("name" -> "Best Tenerife", "board" -> "AI", "price" -> 220)
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await
  }

  "collapse" - {
    "should be supported in http client" in {
      val resp = http.execute {
        search("collapse" / "hotels") collapse {
          collapseField("board")
        }
      }.await

      resp.totalHits shouldBe 3
      resp.hits.size shouldBe 2
    }
  }
}
