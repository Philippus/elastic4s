package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class TermsQueryTest
  extends FlatSpec
    with DockerTests
    with Matchers {

  client.execute {
    createIndex("lords").mappings(
      mapping("people").fields(
        keywordField("name")
      )
    )
  }.await

  client.execute {
    createIndex("lordsfanclub").mappings(
      mapping("fans").fields(
        keywordField("lordswelike")
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("lords/people") fields ("name" -> "nelson"),
      indexInto("lords/people") fields ("name" -> "edmure"),
      indexInto("lords/people") fields ("name" -> "umber"),
      indexInto("lords/people") fields ("name" -> "byron"),
      indexInto("lordsfanclub/fans") fields ("lordswelike" -> List("nelson", "edmure")) id "lordsAppreciationFanClub"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a terms query" should "find multiple terms using 'or'" in {

    val resp = client.execute {
      search("lords") query termsQuery("name", "nelson", "byron")
    }.await.result

    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set("""{"name":"nelson"}""", """{"name":"byron"}""")
  }

  it should "lookup terms to search from a document in another index" in {
    val resp = client.execute {
      search("lords") query termsQuery("name", List.empty[String])
        .ref("lordsfanclub", "fans", "lordsAppreciationFanClub")
        .path("lordswelike")
    }.await.result

    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set("""{"name":"nelson"}""", """{"name":"edmure"}""")
  }

  it should "return no results when an empty array is passed" in {
    val resp = client.execute {
      search("lords") query termsQuery("name", Seq.empty[String])
    }.await.result

    resp.hits.hits.map(_.sourceAsString).toSet shouldBe Set.empty[String]
  }

}
