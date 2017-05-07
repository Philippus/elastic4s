package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class MultiSearchHttpTest
  extends FlatSpec
    with SharedElasticSugar
    with Matchers
    with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  "a multi search request" should "find matching documents for all queries" in {

    http.execute {
      createIndex("jtull")
    }.await

    http.execute {
      bulk(
        indexInto("jtull/albums") fields ("name" -> "aqualung") id 14,
        indexInto("jtull/albums") fields ("name" -> "passion play") id 51
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await

    val resp = http.execute {
      multi(
        search("jtull") query matchQuery("name", "aqualung"),
        search("jtull") query "passion",
        search("jtull" / "albums") query matchAllQuery()
      )
    }.await

    resp.responses.size shouldBe 3
    resp.size shouldBe 3

    resp.responses.head.hits.hits.head.id shouldBe "14"
    resp.responses.tail.head.hits.hits.head.id shouldBe "51"

    resp.responses.head.totalHits shouldBe 1
    resp.responses.tail.head.totalHits shouldBe 1
    resp.responses.last.totalHits shouldBe 2
  }
}
