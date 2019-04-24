package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.requests.common.{DocumentRef, RefreshPolicy}
import org.scalatest.{FlatSpec, Matchers}

class TermsLookupQueryTest
  extends FlatSpec
    with DockerTests
    with Matchers {

  client.execute {
    createIndex("lords").mappings(
      mapping().fields(
        keywordField("name")
      )
    )
  }.await

  client.execute {
    createIndex("lordsfanclub").mappings(
      mapping().fields(
        keywordField("lordswelike")
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("lords") fields ("name" -> "nelson"),
      indexInto("lords") fields ("name" -> "edmure"),
      indexInto("lords") fields ("name" -> "umber"),
      indexInto("lords") fields ("name" -> "byron"),
      indexInto("lordsfanclub") fields ("lordswelike" -> List("nelson", "edmure")) id "lordsAppreciationFanClub"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a terms lookup query" should "lookup terms to search from a document in another index" in {
    val resp = client.execute {
      search("lords") query termsLookupQuery("name", "lordswelike",
        DocumentRef("lordsfanclub", "fans", "lordsAppreciationFanClub"))
    }.await.result

    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set("""{"name":"nelson"}""", """{"name":"edmure"}""")
  }


}
