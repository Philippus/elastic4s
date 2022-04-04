package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TermsQueryTest
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

  "a terms query" should "find multiple terms using 'or'" in {

    val resp = client.execute {
      search("lords") query termsQuery("name", "nelson", "byron")
    }.await.result

    resp.hits.hits.map(_.sourceAsMap).map(_.apply("name")).toSet shouldBe Set("nelson", "byron")
  }

  it should "lookup terms to search from a document in another index" in {
    val resp = client.execute {
      search("lords") query termsQuery("name", List.empty[String])
        .ref("lordsfanclub", "lordsAppreciationFanClub")
        .path("lordswelike")
    }.await.result

    resp.hits.hits.map(_.sourceAsMap).map(_.apply("name")).toSet shouldBe Set("nelson", "edmure")
  }

  it should "return no results when an empty array is passed" in {
    val resp = client.execute {
      search("lords") query termsQuery("name", Seq.empty[String])
    }.await.result

    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set.empty[String]
  }

}
