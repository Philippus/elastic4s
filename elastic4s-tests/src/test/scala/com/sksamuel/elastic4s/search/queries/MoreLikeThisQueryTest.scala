package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.analyzers.StandardAnalyzer
import com.sksamuel.elastic4s.searches.queries.{ArtificialDocument, MoreLikeThisItem}
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{Matchers, WordSpec}

class MoreLikeThisQueryTest extends WordSpec with Matchers with ElasticSugar {

  client.execute {
    createIndex("drinks").mappings (
      mapping("category") as (
        textField("name") store true analyzer StandardAnalyzer
        ),
      mapping("drink") as (
        textField("text") store true analyzer StandardAnalyzer
        ) parent "a"
    ) shards 3
  }.await

  client.execute {
    bulk(
      indexInto("drinks/category") fields("name" → "alcohol") id 1,
      indexInto("drinks/drink") fields ("text" -> "coors light is a coors beer by molson") id 4 parent "1",
      indexInto("drinks/drink") fields ("text" -> "Anheuser-Busch brews a cider called Strongbow") id 6 parent "1",
      indexInto("drinks/drink") fields ("text" -> "Gordons popular gin UK") id 7 parent "1",
      indexInto("drinks/drink") fields ("text" -> "coors regular is another coors beer by molson") id 8 parent "1",
      indexInto("drinks/drink") fields ("text" -> "Hendricks upmarket gin UK") id 9 parent "1"
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "a more like this query" should {

    "find matches based on input text" in {
      val resp = client.execute {
        search("drinks" / "drink") query {
          moreLikeThisQuery("text")
            .likeTexts("coors") minTermFreq 1 minDocFreq 1
        }
      }.await
      resp.hits.map(_.id).toSet shouldBe Set("4", "8")
    }

    "find matches based on doc refs" in {
      val item = MoreLikeThisItem("drinks", "drink", "4")
      val resp1 = client.execute {
        search("drinks" / "drink").query {
          moreLikeThisQuery("text")
            .likeItems(item.copy(routing = Some("2"))) minTermFreq 1 minDocFreq 1
        }
      }.await
      resp1.hits.map(_.id).toSet shouldBe Set()

      val resp2 = client.execute {
        search("drinks" / "drink").query {
          moreLikeThisQuery("text")
            .likeItems(item.copy(routing = Some("1"))) minTermFreq 1 minDocFreq 1
        }
      }.await
      resp2.hits.map(_.id).toSet shouldBe Set("8")
    }

    "support artifical docs" in {
      val resp = client.execute {
        search("drinks" / "drink").query {
          moreLikeThisQuery("text")
            .artificialDocs(
              ArtificialDocument("drinks", "drink", """{ "text" : "gin" }""", Some("1"))
            ) minTermFreq 1 minDocFreq 1
        }
      }.await
      resp.hits.map(_.id).toSet shouldBe Set("7", "9")
    }
  }
}
