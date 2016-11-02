package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.FlatSpec
import org.scalatest.mockito.MockitoSugar

class CountTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    indexInto("london/landmarks").fields("name" -> "hampton court palace")
  }.await

  client.execute {
    indexInto("london/landmarks").fields("name" -> "tower of london")
  }.await

  client.execute {
    indexInto("london/pubs").fields("name" -> "blue bell")
  }.await

  refresh("london")
  blockUntilCount(3, "london")

  "a search request of size 0" should "return total count when no query is specified" in {
    val resp = client.execute {
      searchIn("london").size(0)
    }.await
    assert(3 === resp.totalHits)
  }

  it should "return the document count for the correct type" in {
    val resp = client.execute {
      searchIn("london" / "landmarks").size(0)
    }.await
    assert(2 === resp.totalHits)
  }

  it should "return the document count based on the specified query" in {
    val resp = client.execute {
      searchIn("london" / "landmarks").size(0).query("tower")
    }.await
    assert(1 === resp.totalHits)
  }
}
