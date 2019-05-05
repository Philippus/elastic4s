package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.analyzers.StandardAnalyzer
import com.sksamuel.elastic4s.requests.common.{DocumentRef, RefreshPolicy}
import com.sksamuel.elastic4s.requests.searches.queries.{ArtificialDocument, MoreLikeThisItem}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class MoreLikeThisQueryTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute(
      ElasticDsl.deleteIndex("mltq")
    ).await
  }

  client.execute {
    createIndex("mltq").mapping(
      properties(
        textField("text") store true analyzer StandardAnalyzer
      )
    ) shards 3
  }.await

  client.execute {
    bulk(
      indexInto("mltq") fields ("text" -> "coors light is a coors beer by molson") id "4" routing "1",
      indexInto("mltq") fields ("text" -> "Anheuser-Busch brews a cider called Strongbow") id "6" routing "1",
      indexInto("mltq") fields ("text" -> "Gordons popular gin UK") id "7" routing "1",
      indexInto("mltq") fields ("text" -> "coors regular is another coors beer by molson") id "8" routing "1",
      indexInto("mltq") fields ("text" -> "Hendricks upmarket gin UK") id "9" routing "1"
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

    "find matches based on doc refs" ignore {
      val ref = DocumentRef("drinks", "4")
      val resp2 = client.execute {
        search("mltq").query {
          moreLikeThisQuery("text")
            .likeItems(MoreLikeThisItem(ref, Some("1"))) minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp2.hits.hits.map(_.id).toSet shouldBe Set("8")
    }

    "find matches based on doc refs with routing" ignore {
      val ref = DocumentRef("drinks", "4")

      // no docs have routing 2, so this should match nothing
      val resp1 = client.execute {
        search("mltq").query {
          moreLikeThisQuery("text")
            .likeItems(MoreLikeThisItem(ref, Some("2"))) minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp1.hits.hits.map(_.id).toSet shouldBe Set()
    }

    "support artifical docs" ignore {
      val resp = client.execute {
        search("mltq").query {
          moreLikeThisQuery("text")
            .artificialDocs(
              ArtificialDocument("drinks", """{ "text" : "upmarket gin" }""")
            ) minTermFreq 1 minDocFreq 1
        }
      }.await.result
      resp.hits.hits.map(_.id).toSet shouldBe Set("7", "9")
    }
  }
}
