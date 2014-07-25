package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

/** @author Stephen Samuel */
class MoreLikeThisTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    create index "drinks" mappings {
      "beer" source true as (
        "name" typed StringType store true analyzer StandardAnalyzer,
        "brand" typed StringType store true analyzer KeywordAnalyzer
      )
    } shards 1
  }.await
  client.execute {
    bulk(
      index into "drinks/beer" fields ("name" -> "coors light", "brand" -> "coors") id 4,
      index into "drinks/beer" fields ("name" -> "bud lite", "brand" -> "bud") id 6,
      index into "drinks/beer" fields ("name" -> "coors regular", "brand" -> "coors") id 8
    )
  }.await

  refresh("drinks")
  blockUntilCount(3, "drinks")

  "a more like this query" should "return closest documents" in {
    val resp = client.execute {
      morelike id 4 in "drinks/beer" minTermFreq 1 percentTermsToMatch 0.2 minDocFreq 1
    }.await
    assert("8" === resp.getHits.getAt(0).id)
  }
}
