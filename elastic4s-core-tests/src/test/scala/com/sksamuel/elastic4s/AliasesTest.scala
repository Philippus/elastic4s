package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConversions._
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.mockito.MockitoSugar

class AliasesTest extends FlatSpec with MockitoSugar with ElasticSugar with Matchers {

  client.execute(
    bulk(
      index into "waterways/rivers" id 11 fields("name" -> "River Lune", "country" -> "England"),
      index into "waterways/rivers" id 12 fields("name" -> "River Dee", "country" -> "England"),
      index into "waterways/rivers" id 21 fields("name" -> "River Dee", "country" -> "Wales"),
      index into "waterways_updated/rivers" id 31 fields("name" -> "Thames", "country" -> "England")
    )
  ).await

  refresh("waterways")
  blockUntilCount(3, "waterways")
  blockUntilCount(1, "waterways_updated")

  client.execute {
    add alias "aquatic_locations" on "waterways"
  }.await

  client.execute {
    add alias "english_waterways" on "waterways" filter termQuery("country", "england")
  }.await

  client.execute {
    add alias "moving_alias" on "waterways"
  }.await

  "waterways index" should "return 'River Dee' in England and Wales for search" in {
    val resp = client.execute {
      searches in "waterways" query "Dee"
    }.await

    assert(2 === resp.totalHits)
    val hitIds = resp.hits.map(hit => hit.id).toList.sorted
    assert(hitIds === Array("12", "21"))
  }

  "aquatic_locations alias" should "get 'River Dee (Wales)' from waterways/rivers" in {
    val resp = client.execute {
      get id 21 from "aquatic_locations/rivers"
    }.await
    assert("21" === resp.id)
  }

  "english_waterways alias" should "return 'River Dee' in England for search" in {
    val resp = client.execute {
      searches in "english_waterways" query "Dee"
    }.await

    assert(1 === resp.totalHits)
    assert("12" === resp.hits.head.id)
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
      get alias "moving_alias" on("waterways", "waterways_updated")
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
      get alias "moving_alias" on("waterways", "waterways_updated")
    }.await

    compareAliasesForIndex(respAfterMovingAlias, "waterways_updated", Set("moving_alias"))
    assert(!respAfterMovingAlias.getAliases.containsKey("waterways"))
  }

  "get alias" should "have implicit conversion to rich response" in {

    val resp = client.execute {
      get alias "english_waterways" on "waterways"
    }.await

    resp.aliases("waterways").head.alias shouldBe "english_waterways"
  }

  private def compareAliasesForIndex(resp: GetAliasesResponse, index: String,
                                     expectedAliases: Set[String]) = {
    val aliases = resp.getAliases.get(index)
    assert(aliases !== null)
    assert(expectedAliases === aliases.map(_.alias()).toSet)
  }
}
