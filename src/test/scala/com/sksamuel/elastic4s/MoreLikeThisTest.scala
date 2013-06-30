package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import com.sksamuel.elastic4s.FieldType.StringType
import com.sksamuel.elastic4s.Analyzer.{KeywordAnalyzer, StandardAnalyzer}

/** @author Stephen Samuel */
class MoreLikeThisTest extends FlatSpec with MockitoSugar with ElasticSugar {

    client.sync.execute {
        create index "beer" mappings {
            "lager" source true as (
              "name" typed StringType store true analyzer StandardAnalyzer,
              "brand" typed StringType store true analyzer KeywordAnalyzer
              )
        }
    }
    client.sync.execute {
        index into "beer/lager" fields (
          "name" -> "coors light",
          "brand" -> "coors"
          ) id 4
    }
    client.sync.execute {
        index into "beer/lager" fields (
          "name" -> "bud lite",
          "brand" -> "bud"
          ) id 6
    }
    client.sync.execute {
        index into "beer/lager" fields (
          "name" -> "coors regular",
          "brand" -> "coors"
          ) id 8
    }
    refresh("beer")
    blockUntilCount(3, "beer")

    "a more like this query" should "return closest documents" in {
        val resp = client.sync.execute {
            morelike id 4 in "beer/lager" minTermFreq 1 percentTermsToMatch 0.2 minDocFreq 1
        }
        assert("8" === resp.getHits.getAt(0).id)
    }
}
