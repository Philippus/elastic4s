package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.{DocumentRef, RefreshPolicy}
import com.sksamuel.elastic4s.requests.searches.queries.{ArtificialDocument, MoreLikeThisItem}
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.analyzers.StandardAnalyzer
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class MoreLikeThisQueryTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute(
      ElasticDsl.deleteIndex("drinks")
    ).await
  }

  client.execute {
    createIndex("drinks").mappings (
      mapping("drink") as (
        textField("text") store true analyzer StandardAnalyzer
        )
    ) shards 3
  }.await

  client.execute {
    bulk(
      indexInto("drinks/drink") fields ("text" -> "coors light is a coors beer by molson") id "4" routing "1",
      indexInto("drinks/drink") fields ("text" -> "Anheuser-Busch brews a cider called Strongbow") id "6" routing "1",
      indexInto("drinks/drink") fields ("text" -> "Gordons popular gin UK") id "7" routing "1",
      indexInto("drinks/drink") fields ("text" -> "coors regular is another coors beer by molson") id "8" routing "1",
      indexInto("drinks/drink") fields ("text" -> "Hendricks upmarket gin UK") id "9" routing "1"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a more like this query" should {

    "find matches based on input text" in {
      val resp = client.execute {
        search("drinks") query {
          moreLikeThisQuery("text")
            .likeTexts("coors") minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp.hits.hits.map(_.id).toSet shouldBe Set("4", "8")
    }

    "find matches based on doc refs" in {
      val ref = DocumentRef("drinks", "drink", "4")

      val resp1 = client.execute {
        search("drinks").query {
          moreLikeThisQuery("text")
            .likeItems(MoreLikeThisItem(ref, Some("2"))) minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp1.hits.hits.map(_.id).toSet shouldBe Set()

      val resp2 = client.execute {
        search("drinks").query {
          moreLikeThisQuery("text")
            .likeItems(MoreLikeThisItem(ref, Some("1"))) minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp2.hits.hits.map(_.id).toSet shouldBe Set("8")
    }

    "support artifical docs" in {
      val resp = client.execute {
        search("drinks").query {
          moreLikeThisQuery("text")
            .artificialDocs(
              ArtificialDocument("drinks", "drink", """{ "text" : "gin" }""", Some("1"))
            ) minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp.hits.hits.map(_.id).toSet shouldBe Set("7", "9")
    }
  }
}
