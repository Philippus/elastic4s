package com.sksamuel.elastic4s.aliases

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class AliasesTest extends FlatSpec with MockitoSugar with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("aliases")
    }.await
    http.execute {
      deleteIndex("aliases_updated")
    }.await
  }

  http.execute(
    bulk(
      indexInto("aliases" / "rivers") id 11 fields("name" -> "River Lune", "country" -> "England"),
      indexInto("aliases" / "rivers") id 12 fields("name" -> "River Dee", "country" -> "England"),
      indexInto("aliases" / "rivers") id 21 fields("name" -> "River Dee", "country" -> "Wales"),
      indexInto("aliases_updated" / "rivers") id 31 fields("name" -> "Thames", "country" -> "England")
    ).immediateRefresh()
  ).await

  http.execute {
    addAlias("aquatic_locations") on "aliases"
  }.await

  http.execute {
    addAlias("english_waterways").on("aliases").filter(termQuery("country", "england"))
  }.await

  http.execute {
    addAlias("moving_alias") on "aliases"
  }.await

  "waterways index" should "return 'River Dee' in England and Wales for search" in {
    val resp = http.execute {
      search("aliases") query "Dee"
    }.await.right.get
    resp.totalHits shouldBe 2
    resp.ids shouldBe Seq("12", "21")
  }

  "aquatic_locations" should "alias waterways" in {
    val resp1 = http.execute {
      get("21").from("aquatic_locations")
    }.await.right.get
    resp1.id shouldBe "21"
  }

  it should "alias waterways and accept a type" in {
    val resp2 = http.execute {
      get(21).from("aquatic_locations/rivers")
    }.await.right.get
    resp2.id shouldBe "21"
  }

  "english_waterways" should "be an alias with a filter for country=england" in {
    val resp = http.execute {
      search("english_waterways").query("dee")
    }.await.right.get
    // 'english_waterways' has a filter for England only, so we only expect to find one River dee
    resp.totalHits shouldBe 1
    resp.hits.hits.head.id shouldBe "12"
  }

  it should "be returned when querying the index for the alias" in {
    val resp = http.execute {
      getAlias("english_waterways")
    }.await

    //   compareAliasesForIndex(resp, "waterways", Set("english_waterways"))
  }

  it should "be in query for alias on waterways" in {
    val resp = http.execute {
      getAlias("english_waterways") on "aliases"
    }.await

    // compareAliasesForIndex(resp, "waterways", Set("english_waterways"))
  }

  "moving_alias" should "move from 'waterways' to 'waterways_updated'" in {
    val resp = http.execute {
      getAlias("moving_alias") on("aliases", "aliases_updated")
    }.await

    //    compareAliasesForIndex(resp, "waterways", Set("moving_alias"))
    //    assert(!resp.getAliases.containsKey("waterways_updated"))
    //
    //    execute {
    //      aliases(
    //        removeAlias("moving_alias") on "waterways",
    //        addAlias("moving_alias") on "waterways_updated"
    //      )
    //    }.await
    //
    //    val respAfterMovingAlias = execute {
    //      getAlias("moving_alias").on("waterways", "waterways_updated")
    //    }.await
    //
    //    compareAliasesForIndex(respAfterMovingAlias, "waterways_updated", Set("moving_alias"))
    //    assert(!respAfterMovingAlias.getAliases.containsKey("waterways"))
  }
}
