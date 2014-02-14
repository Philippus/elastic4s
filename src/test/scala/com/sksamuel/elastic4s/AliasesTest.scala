package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.common.Priority
import org.elasticsearch.index.query.FilterBuilders
import scala.collection.JavaConversions._

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
    aliases add "aquatic_locations" on "waterways"
  }

  client.sync.execute {
    aliases add "english_waterways" on "waterways" filter FilterBuilders.termFilter("country", "england")
  }

  "waterways index" should "return 'River Dee' in England and Wales for search" in {
    val resp = client.sync.execute {
      search in "waterways" query "Dee"
    }

    assert(2 === resp.getHits.totalHits())
    val hitIds = resp.getHits.map(hit => hit.id()).toList.sorted
    assert(hitIds === Array("12", "21"))
  }

  "aquatic_locations alias" should "get 'River Dee (Wales)' from waterways/rivers" in {
    val resp = client.sync.execute {
      get id 21 from "aquatic_locations/rivers"
    }
    assert("21" === resp.getId)
  }

  "english_waterways alias" should "return 'River Dee' in England for search" in {
    val resp = client.sync.execute {
      search in "english_waterways" query "Dee"
    }

    assert(1 === resp.getHits.totalHits())
    assert("12" === resp.getHits.getAt(0).id())
  }

  it should "be in query for alias" in {
    val resp = client.sync.execute {
      aliases get "english_waterways"
    }

    val waterwaysAliases = resp.getAliases.get("waterways")
    assert(waterwaysAliases !== null)
    assert(1 === waterwaysAliases.size)
    assert("english_waterways" === waterwaysAliases.head.alias())
  }

  it should "be in query for alias on waterways" in {
    val resp = client.sync.execute {
      aliases get "english_waterways" on "waterways"
    }

    val waterwaysAliases = resp.getAliases.get("waterways")
    assert(waterwaysAliases !== null)
    assert(1 === waterwaysAliases.size)
    assert("english_waterways" === waterwaysAliases.head.alias())
  }
}
