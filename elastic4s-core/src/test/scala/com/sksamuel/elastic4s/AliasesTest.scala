package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.index.query.FilterBuilders
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar

import scala.collection.JavaConversions._

class AliasesTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute(
    bulk(
      index into "waterways/rivers" id 11 fields ("name" -> "River Lune", "country" -> "England"),
      index into "waterways/rivers" id 12 fields ("name" -> "River Dee", "country" -> "England"),
      index into "waterways/rivers" id 21 fields ("name" -> "River Dee", "country" -> "Wales"),
      index into "waterways_updated/rivers" id 31 fields ("name" -> "Thames", "country" -> "England")
    )
  ).await

  refresh("waterways")
  blockUntilCount(3, "waterways")
  blockUntilCount(1, "waterways_updated")

  client.execute {
    add alias "aquatic_locations" on "waterways"
  }.await

  client.execute {
    add alias "english_waterways" on "waterways" filter FilterBuilders.termFilter("country", "england")
  }.await

  client.execute {
    add alias "moving_alias" on "waterways"
  }.await

  "waterways index" should "return 'River Dee' in England and Wales for search" in {
    val resp = client.execute {
      search in "waterways" query "Dee"
    }.await

    assert(2 === resp.getHits.totalHits())
    val hitIds = resp.getHits.map(hit => hit.id()).toList.sorted
    assert(hitIds === Array("12", "21"))
  }

  "aquatic_locations alias" should "get 'River Dee (Wales)' from waterways/rivers" in {
    val resp = client.execute {
      get id 21 from "aquatic_locations/rivers"
    }.await
    assert("21" === resp.getId)
  }

  "english_waterways alias" should "return 'River Dee' in England for search" in {
    val resp = client.execute {
      search in "english_waterways" query "Dee"
    }.await

    assert(1 === resp.getHits.totalHits())
    assert("12" === resp.getHits.getAt(0).id())
  }

  it should "be in query for alias" in {
    val resp = client.execute {
      get alias "english_waterways"
    }.await

    compareAliasesForIndex(resp, "waterways", Set("english_waterways"))
  }

  it should "be in query for alias on waterways" in {
    val resp = client.execute {
      get alias "english_waterways" on "waterways"
    }.await

    compareAliasesForIndex(resp, "waterways", Set("english_waterways"))
  }

  "moving_alias" should "move from 'waterways' to 'waterways_updated'" in {
    val resp = client.execute {
      get alias "moving_alias" on ("waterways", "waterways_updated")
    }.await

    compareAliasesForIndex(resp, "waterways", Set("moving_alias"))
    assert(!resp.getAliases.containsKey("waterways_updated"))

    client.execute {
      aliases(
        remove alias "moving_alias" on "waterways",
        add alias "moving_alias" on "waterways_updated"
      )
    }.await

    val respAfterMovingAlias = client.execute {
      get alias "moving_alias" on ("waterways", "waterways_updated")
    }.await

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
