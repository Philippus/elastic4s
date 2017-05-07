package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class TermsQueryTest
  extends FlatSpec
    with SharedElasticSugar
    with Matchers
    with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("lords").mappings(
      mapping("people").fields(
        keywordField("name")
      )
    )
  }.await

  http.execute {
    bulk(
      indexInto("lords/people") fields ("name" -> "nelson"),
      indexInto("lords/people") fields ("name" -> "edmure"),
      indexInto("lords/people") fields ("name" -> "umber"),
      indexInto("lords/people") fields ("name" -> "byron")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "a terms query" should "find multiple terms using 'or'" in {

    val resp = http.execute {
      search("lords") query termsQuery("name", "nelson", "byron")
    }.await

    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set("""{"name":"nelson"}""", """{"name":"byron"}""")
  }
}
