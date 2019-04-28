package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.FlatSpec

import scala.util.Try

class CountTest extends FlatSpec with DockerTests {

  Try {
    client.execute {
      deleteIndex("london")
    }.await
  }

  client.execute {
    bulk(
      indexInto("london").fields("name" -> "hampton court palace"),
      indexInto("london").fields("name" -> "tower of london")
    ).refreshImmediately
  }.await

  "a search request of size 0" should "return total count when no query is specified" in {
    val resp = client.execute {
      search("london").size(0)
    }.await.result
    assert(2 === resp.totalHits)
  }

  it should "return the document count for the correct type" in {
    val resp = client.execute {
      search("london").size(0)
    }.await.result
    assert(2 === resp.totalHits)
  }

  it should "return the document count based on the specified query" in {
    val resp = client.execute {
      search("london").size(0).query("tower")
    }.await.result
    assert(1 === resp.totalHits)
  }
}
