package com.sksamuel.elastic4s.requests.count

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class CountTest extends AnyWordSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("stads")
    }.await
  }

  Try {
    client.execute {
      deleteIndex("stads2")
    }.await
  }

  Try {
    client.execute {
      deleteIndex("stads3")
    }.await
  }

  client.execute {
    createIndex("stads")
  }.await

  client.execute {
    createIndex("stads2")
  }.await

  client.execute {
    createIndex("stads3")
  }.await

  client.execute {
    bulk(
      indexInto("stads").fields("name"  -> "riverside stadium"),
      indexInto("stads").fields("name"  -> "stadium of shite"),
      indexInto("stads").fields("name"  -> "sports arena dot com ashley stadium"),
      indexInto("stads").fields("name"  -> "macron"),
      indexInto("stads").fields("name"  -> "old trafford"),
      indexInto("stads").fields("name"  -> "pride park"),
      indexInto("stads").fields("name"  -> "hillsborough"),
      indexInto("stads").fields("name"  -> "KCom Stadium"),
      indexInto("stads").fields("name"  -> "Anfield"),
      indexInto("stads2").fields("name" -> "Stamford Bridge"),
      indexInto("stads3").fields("name" -> "AMEX Stadium")
    ).refreshImmediately
  }.await

  "a count query" should {
    "count all docs in a specified index" in {
      client.execute {
        count("stads")
      }.await.result.count shouldBe 9
    }
    "count all docs across multiple specified indexes" in {
      client.execute {
        count(Seq("stads2", "stads3"))
      }.await.result.count shouldBe 2
    }
    "count with a filter" in {
      client.execute {
        count("stads").filter(prefixQuery("name", "river"))
      }.await.result.count shouldBe 1
    }
    "count with type set" in {
      client.execute {
        count("stads")
      }.await.result.count shouldBe 9
    }
  }
}
