package com.sksamuel.elastic4s.count

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class CountTest extends WordSpec with DiscoveryLocalNodeProvider with ElasticDsl with Matchers {

  Try {
    http.execute {
      deleteIndex("stadiums")
    }.await
  }

  Try {
    http.execute {
      deleteIndex("teams")
    }.await
  }

  Try {
    http.execute {
      deleteIndex("players")
    }.await
  }

  http.execute {
    createIndex("stadiums")
  }.await

  http.execute {
    createIndex("teams")
  }.await

  http.execute {
    createIndex("players")
  }.await

  http.execute {
    bulk(
      indexInto("stadiums/stadiums").fields("name" -> "riverside stadium"),
      indexInto("stadiums/stadiums").fields("name" -> "stadium of shite"),
      indexInto("stadiums/stadiums").fields("name" -> "sports arena dot com ashley stadium"),
      indexInto("stadiums/stadiums").fields("name" -> "macron"),
      indexInto("stadiums/stadiums").fields("name" -> "old trafford"),
      indexInto("stadiums/stadiums").fields("name" -> "pride park"),
      indexInto("stadiums/stadiums").fields("name" -> "hillsborough"),
      indexInto("stadiums/stadiums").fields("name" -> "KCom Stadium"),
      indexInto("stadiums/stadiums").fields("name" -> "Anfield"),
      indexInto("teams/teams").fields("name" -> "Boro"),
      indexInto("players/players").fields("name" -> "Assombalonga")
    ).immediateRefresh()
  }.await

  "a count query" should {
    "count all docs" in {
      http.execute {
        count("_all")
      }.await.right.get.count shouldBe 11
    }
    "count all docs in a specified index" in {
      http.execute {
        count("teams")
      }.await.right.get.count shouldBe 1
    }
    "count all docs across multiple specified indexes" in {
      http.execute {
        count(Seq("teams", "players"))
      }.await.right.get.count shouldBe 2
    }
    "count with a filter" in {
      http.execute {
        count("_all").filter(prefixQuery("name", "river"))
      }.await.right.get.count shouldBe 1
    }
    "count with type set" in {
      http.execute {
        count("stadiums", "stadiums")
      }.await.right.get.count shouldBe 9
      http.execute {
        count("stadiums", "nonexisting")
      }.await.right.get.count shouldBe 0
    }
  }
}
