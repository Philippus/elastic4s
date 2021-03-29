package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.{DocumentRef, RefreshPolicy}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TermsLookupQueryTest
  extends AnyFlatSpec
    with DockerTests
    with Matchers {

  client.execute {
    createIndex("lords").mapping(
      mapping(
        keywordField("name")
      )
    )
  }.await

  client.execute {
    createIndex("lordsfanclub").mapping(
      mapping(
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
        DocumentRef("lordsfanclub", "lordsAppreciationFanClub"))
    }.await.result

    resp.hits.hits.head.sourceAsMap("name") shouldBe "nelson"
    resp.hits.hits.apply(1).sourceAsMap("name") shouldBe "edmure"
  }


}
