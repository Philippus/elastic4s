package com.sksamuel.elastic4s.count

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class CountTest extends WordSpec with DiscoveryLocalNodeProvider with ElasticDsl with Matchers {

  Try {
    http.execute {
      deleteIndex("stads")
    }.await
  }

  Try {
    http.execute {
      deleteIndex("stads2")
    }.await
  }

  Try {
    http.execute {
      deleteIndex("stads3")
    }.await
  }

  http.execute {
    createIndex("stads")
  }.await

  http.execute {
    createIndex("stads2")
  }.await

  http.execute {
    createIndex("stads3")
  }.await

  http.execute {
    bulk(
      indexInto("stads/stads").fields("name" -> "riverside stadium"),
      indexInto("stads/stads").fields("name" -> "stadium of shite"),
      indexInto("stads/stads").fields("name" -> "sports arena dot com ashley stadium"),
      indexInto("stads/stads").fields("name" -> "macron"),
      indexInto("stads/stads").fields("name" -> "old trafford"),
      indexInto("stads/stads").fields("name" -> "pride park"),
      indexInto("stads/stads").fields("name" -> "hillsborough"),
      indexInto("stads/stads").fields("name" -> "KCom Stadium"),
      indexInto("stads/stads").fields("name" -> "Anfield"),
      indexInto("stads2/stads2").fields("name" -> "Stamford Bridge"),
      indexInto("stads3/stads3").fields("name" -> "AMEX Stadium")
    ).immediateRefresh()
  }.await

  "a count query" should {
    "count all docs in a specified index" in {
      http.execute {
        count("stads")
      }.await.right.get.result.count shouldBe 9
    }
    "count all docs across multiple specified indexes" in {
      http.execute {
        count(Seq("stads2", "stads3"))
      }.await.right.get.result.count shouldBe 2
    }
    "count with a filter" in {
      http.execute {
        count("stads").filter(prefixQuery("name", "river"))
      }.await.right.get.result.count shouldBe 1
    }
    "count with type set" in {
      http.execute {
        count("stads", "stads")
      }.await.right.get.result.count shouldBe 9
      http.execute {
        count("stads", "nonexisting")
      }.await.right.get.result.count shouldBe 0
    }
  }
}
