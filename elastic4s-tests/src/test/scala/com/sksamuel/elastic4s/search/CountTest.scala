package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.FlatSpec

import scala.util.Try

class CountTest extends FlatSpec with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      deleteIndex("london")
    }.await
  }

  http.execute {
    bulk(
      indexInto("london/landmarks").fields("name" -> "hampton court palace"),
      indexInto("london/landmarks").fields("name" -> "tower of london")
    ).immediateRefresh()
  }.await

  "a search request of size 0" should "return total count when no query is specified" in {
    val resp = http.execute {
      search("london").size(0)
    }.await.get
    assert(2 === resp.totalHits)
  }

  it should "return the document count for the correct type" in {
    val resp = http.execute {
      search("london" / "landmarks").size(0)
    }.await.get
    assert(2 === resp.totalHits)
  }

  it should "return the document count based on the specified query" in {
    val resp = http.execute {
      search("london" / "landmarks").size(0).query("tower")
    }.await.get
    assert(1 === resp.totalHits)
  }
}
