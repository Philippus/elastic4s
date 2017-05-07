package com.sksamuel.elastic4s.search.collapse

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
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
          keywordField("name"),
          keywordField("board")
        )
      }
    }.await

    http.execute {
      bulk(
        indexInto("collapse" / "hotels") id "1" fields("name" -> "Ibiza Playa", "board" -> "AI"),
        indexInto("collapse" / "hotels") id "2" fields("name" -> "Ibiza Playa", "board" -> "BB"),

        indexInto("collapse" / "hotels") id "3" fields("name" -> "Best Tenerife", "board" -> "AI")
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
