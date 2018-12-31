package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.analyzers.StandardAnalyzer
import com.sksamuel.elastic4s.requests.searches.queries.ArtificialDocument
import com.sksamuel.elastic4s.testkit.{DockerTests, ElasticMatchers}
import com.sksamuel.elastic4s.requests.common.{DocumentRef, RefreshPolicy}
import org.scalatest.{Matchers, WordSpec}

class MoreLikeThisQueryHttpTest
  extends WordSpec
    with Matchers
    with DockerTests
    with ElasticMatchers {

  client.execute {
    createIndex("mltq").mappings {
      mapping("alcohol") source true as (
        textField("name") store true analyzer StandardAnalyzer
        )
    } shards 1
  }.await

  client.execute {
    bulk(
      indexInto("mltq/alcohol") fields ("text" -> "coors light is a coors beer by molson") id "4",
      indexInto("mltq/alcohol") fields ("text" -> "Anheuser-Busch brews a cider called Strongbow") id "6",
      indexInto("mltq/alcohol") fields ("text" -> "Gordons popular gin UK") id "7",
      indexInto("mltq/alcohol") fields ("text" -> "coors regular is another coors beer by molson") id "8",
      indexInto("mltq/alcohol") fields ("text" -> "Hendricks upmarket gin UK") id "9"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a more like this query" should {

    "find matches based on input text" in {
      val resp = client.execute {
        search("mltq") query {
          moreLikeThisQuery("text")
            .likeTexts("coors") minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp.hits.hits.map(_.id).toSet shouldBe Set("4", "8")
    }

    "find matches based on doc refs" in {
      val resp = client.execute {
        search("mltq").query {
          moreLikeThisQuery("text")
            .likeDocs(DocumentRef("mltq", "alcohol", "4")) minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp.hits.hits.map(_.id).toSet shouldBe Set("8")
    }

    "support artifical docs" in {
      val resp = client.execute {
        search("mltq").query {
          moreLikeThisQuery("text")
            .artificialDocs(ArtificialDocument("mltq", "alcohol", """{ "text" : "gin" }""")) minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp.hits.hits.map(_.id).toSet shouldBe Set("7", "9")
    }
  }
}
