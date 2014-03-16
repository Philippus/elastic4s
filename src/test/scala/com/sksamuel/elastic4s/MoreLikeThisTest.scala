package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import com.sksamuel.elastic4s.mapping.FieldType.StringType
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class MoreLikeThisTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.sync.execute {
    create index "drinks" mappings {
      "beer" source true as (
        "name" typed StringType store true analyzer StandardAnalyzer,
        "brand" typed StringType store true analyzer KeywordAnalyzer
      )
    }
  }
  client.sync.execute {
    index into "drinks/beer" fields (
      "name" -> "coors light",
      "brand" -> "coors"
    ) id 4
  }
  client.sync.execute {
    index into "drinks/beer" fields (
      "name" -> "bud lite",
      "brand" -> "bud"
    ) id 6
  }
  client.sync.execute {
    index into "drinks/beer" fields (
      "name" -> "coors regular",
      "brand" -> "coors"
    ) id 8
  }

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("drinks")
  blockUntilCount(3, "drinks")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "a more like this query" should "return closest documents" in {
    val resp = client.sync.execute {
      morelike id 4 in "drinks/beer" minTermFreq 1 percentTermsToMatch 0.2 minDocFreq 1
    }
    assert("8" === resp.getHits.getAt(0).id)
  }
}
