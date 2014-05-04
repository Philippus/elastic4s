package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.common.Priority
import org.elasticsearch.index.query.FilterBuilders
import scala.collection.JavaConversions._
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse

class AliasesTest extends FlatSpec with MockitoSugar with ElasticSugar {
  client.bulk(
    index into "waterways/rivers" id 11 fields (
      "name" -> "River Lune",
      "country" -> "England"
    ),
    index into "waterways/rivers" id 12 fields (
      "name" -> "River Dee",
      "country" -> "England"
    ),
    index into "waterways/rivers" id 21 fields (
      "name" -> "River Dee",
      "country" -> "Wales"
    ),
    index into "waterways_updated/rivers" id 31 fields (
      "name" -> "Thames",
      "country" -> "England"
    )
  )

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("waterways")
  blockUntilCount(3, "waterways")
  blockUntilCount(1, "waterways_updated")

  client.sync.execute {
    aliases add "aquatic_locations" on "waterways"
  }

  client.sync.execute {
    aliases add "english_waterways" on "waterways" filter FilterBuilders.termFilter("country", "england")
  }

  client.sync.execute {
    aliases add "moving_alias" on "waterways"
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

    compareAliasesForIndex(resp, "waterways", Set("english_waterways"))
  }

  it should "be in query for alias on waterways" in {
    val resp = client.sync.execute {
      aliases get "english_waterways" on "waterways"
    }

    compareAliasesForIndex(resp, "waterways", Set("english_waterways"))
  }

  "moving_alias" should "move from 'waterways' to 'waterways_updated'" in {
    val resp = client.sync.execute {
      aliases get "moving_alias" on ("waterways", "waterways_updated")
    }
    compareAliasesForIndex(resp, "waterways", Set("moving_alias"))
    assert(!resp.getAliases.containsKey("waterways_updated"))

    client.sync.execute {
      aliases(
        aliases remove "moving_alias" on "waterways",
        aliases add "moving_alias" on "waterways_updated"
      )
    }

    val respAfterMovingAlias = client.sync.execute {
      aliases get "moving_alias" on ("waterways", "waterways_updated")
    }
    compareAliasesForIndex(respAfterMovingAlias, "waterways_updated", Set("moving_alias"))
    assert(!respAfterMovingAlias.getAliases.containsKey("waterways"))
  }

  private def compareAliasesForIndex(resp: GetAliasesResponse, index: String,
                                     expectedAliases: Set[String]) = {
    val aliases = resp.getAliases.get(index)
    assert(aliases !== null)
    assert(expectedAliases === aliases.map(_.alias()).toSet)
  }
}
