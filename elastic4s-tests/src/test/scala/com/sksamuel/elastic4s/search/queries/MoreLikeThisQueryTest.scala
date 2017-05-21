package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.{DocumentRef, ElasticDsl, RefreshPolicy}
import com.sksamuel.elastic4s.analyzers.StandardAnalyzer
import com.sksamuel.elastic4s.searches.queries.ArtificialDocument
import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, ElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class MoreLikeThisQueryTest extends WordSpec with Matchers with ElasticSugar with ClassloaderLocalNodeProvider with ElasticDsl {

  client.execute {
    createIndex("drinks").mappings {
      mapping("alcohol") source true as (
        textField("name") store true analyzer StandardAnalyzer
      )
    } shards 1
  }.await

  client.execute {
    bulk(
      indexInto("drinks/alcohol") fields ("text" -> "coors light is a coors beer by molson") id 4,
      indexInto("drinks/alcohol") fields ("text" -> "Anheuser-Busch brews a cider called Strongbow") id 6,
      indexInto("drinks/alcohol") fields ("text" -> "Gordons popular gin UK") id 7,
      indexInto("drinks/alcohol") fields ("text" -> "coors regular is another coors beer by molson") id 8,
      indexInto("drinks/alcohol") fields ("text" -> "Hendricks upmarket gin UK") id 9
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a more like this query" should {

    "find matches based on input text" in {
      val resp = client.execute {
        search("drinks" / "alcohol") query {
          moreLikeThisQuery("text")
            .likeTexts("coors") minTermFreq 1 minDocFreq 1
        }
      }.await
      resp.hits.map(_.id).toSet shouldBe Set("4", "8")
    }

    "find matches based on doc refs" in {
      val resp = client.execute {
        search("drinks" / "alcohol").query {
          moreLikeThisQuery("text")
            .likeDocs(DocumentRef("drinks", "alcohol", "4")) minTermFreq 1 minDocFreq 1
        }
      }.await
      resp.hits.map(_.id).toSet shouldBe Set("8")
    }

    "support artifical docs" in {
      val resp = client.execute {
        search("drinks" / "alcohol").query {
          moreLikeThisQuery("text")
            .artificialDocs(ArtificialDocument("drinks", "alcohol", """{ "text" : "gin" }""")) minTermFreq 1 minDocFreq 1
        }
      }.await
      resp.hits.map(_.id).toSet shouldBe Set("7", "9")
    }
  }
}
