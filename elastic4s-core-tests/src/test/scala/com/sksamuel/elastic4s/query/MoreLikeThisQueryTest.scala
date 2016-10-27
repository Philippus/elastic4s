package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.Item
import com.sksamuel.elastic4s.analyzers.{KeywordAnalyzer, StandardAnalyzer}
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class MoreLikeThisQueryTest extends WordSpec with Matchers with ElasticSugar {

  client.execute {
    create index "drinks" mappings {
      "beer" source true as(
        "name" typed StringType store true analyzer StandardAnalyzer,
        "brand" typed StringType store true analyzer KeywordAnalyzer
        )
    } shards 1
  }.await

  client.execute {
    bulk(
      index into "drinks/beer" fields ("text" -> "coors light is a beer by molson") id 4,
      index into "drinks/beer" fields ("text" -> "bud lite is brewed by Anheuser-Busch") id 6,
      index into "drinks/beer" fields ("text" -> "coors regular is another coors beer by molson") id 8
    )
  }.await

  refresh("drinks")
  blockUntilCount(3, "drinks")

  "a more like this query" should {

    "find matches based on input text" in {
      val resp = client.execute {
        search in "drinks/beer" query {
          moreLikeThisQuery("text") like("coors", "beer", "molson") minTermFreq 1 minDocFreq 1
        }
      }.await
      resp.hits.map(_.id) should contain("4")
      resp.hits.map(_.id) should contain("8")
    }

    "find matches based on input items" in {
      val resp = client.execute {
        search in "drinks/beer" query {
          moreLikeThisQuery("text") like MoreLikeThisItem("drinks", "beer", "4") minTermFreq 1 minDocFreq 1
        }
      }.await
      resp.hits.map(_.id) should contain("8")
    }
  }
}
