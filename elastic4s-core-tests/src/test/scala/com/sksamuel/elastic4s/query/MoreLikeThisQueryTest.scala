package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.anaylzers.{KeywordAnalyzer, StandardAnalyzer}
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

/** @author Stephen Samuel */
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
      index into "drinks/beer" fields ("text" -> "coors regular is another beer by molson") id 8
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
      println(resp.hits.toList)
      resp.hits.map(_.id) should contain("4")
      resp.hits.map(_.id) should contain("8")
    }
  }
}
