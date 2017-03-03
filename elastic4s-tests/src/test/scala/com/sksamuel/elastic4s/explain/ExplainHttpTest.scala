package com.sksamuel.elastic4s.explain

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.ResponseException
import org.scalatest.{FlatSpec, Matchers}

class ExplainHttpTest
  extends FlatSpec
    with SharedElasticSugar
    with Matchers
    with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    bulk(
      indexInto("explain/kings") fields ("name" -> "richard") id 4,
      indexInto("explain/kings") fields ("name" -> "edward") id 5
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "an explain request" should "explain a matching document" in {

    val resp = http.execute {
      explain("explain", "kings", "4") query termQuery("name", "richard")
    }.await

    resp.isMatch shouldBe true
    resp.explanation.details.head.description shouldBe "weight(name:richard in 0) [PerFieldSimilarity], result of:"
  }

  it should "not explain a not found document" in {
    intercept[ResponseException] {
      http.execute {
        explain("explain", "kings", "24") query termQuery("name", "edward")
      }.await
    }
  }
}
