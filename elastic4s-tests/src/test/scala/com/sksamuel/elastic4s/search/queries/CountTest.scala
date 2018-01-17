package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.FlatSpec

import scala.util.Try

class CountTest extends FlatSpec with DockerTests {

  Try {
    http.execute {
      deleteIndex("london")
    }.await
  }

  http.execute {
    bulk(
      indexInto("london/landmarks").fields("name" -> "hampton court palace"),
      indexInto("london/landmarks").fields("name" -> "tower of london")
    ).refreshImmediately
  }.await

  "a search request of size 0" should "return total count when no query is specified" in {
    val resp = http.execute {
      search("london").size(0)
    }.await.right.get.result
    assert(2 === resp.totalHits)
  }

  it should "return the document count for the correct type" in {
    val resp = http.execute {
      search("london").size(0)
    }.await.right.get.result
    assert(2 === resp.totalHits)
  }

  it should "return the document count based on the specified query" in {
    val resp = http.execute {
      search("london").size(0).query("tower")
    }.await.right.get.result
    assert(1 === resp.totalHits)
  }
}
