package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.common.Priority
import org.elasticsearch.index.query.FilterBuilders

class AliasesTest extends FlatSpec with MockitoSugar with ElasticSugar {
  client.bulk(
    index into "waterways/rivers" id 11 fields(
      "name" -> "River Lune",
      "country" -> "England"
      ),
    index into "waterways/rivers" id 12 fields(
      "name" -> "River Dee",
      "country" -> "England"
      ),
    index into "waterways/rivers" id 21 fields(
      "name" -> "River Dee",
      "country" -> "Wales"
      )
  )

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("waterways")
  blockUntilCount(3, "waterways")

  client.sync.execute {
    addAlias("waterways", "aquatic_locations")
  }

  client.sync.execute {
    addAlias("waterways", "english_waterways", FilterBuilders.termFilter("country", "england"))
  }

  "get 'River Lune' on english_waterways/rivers" should "return 'River Lune' from waterways/rivers" in {
    val resp = client.sync.execute {
      get id 11 from "english_waterways/rivers"
    }
    assert("11" === resp.getId)
  }

  "search for 'Dee' on english_waterways" should "return 'River Dee' in England" in {
    val resp = client.sync.execute {
      search in "english_waterways" query "Dee"
    }

    assert(1 === resp.getHits.totalHits())
    assert("12" === resp.getHits.getAt(0).id())
  }
}
