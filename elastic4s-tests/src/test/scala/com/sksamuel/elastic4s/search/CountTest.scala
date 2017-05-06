package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.FlatSpec

class CountTest extends FlatSpec with ElasticDsl with DualElasticSugar with DualClient {

  override protected def beforeRunTests(): Unit = {
    execute {
      indexInto("london/landmarks").fields("name" -> "hampton court palace")
    }.await

    execute {
      indexInto("london/landmarks").fields("name" -> "tower of london")
    }.await

    execute {
      indexInto("london/pubs").fields("name" -> "blue bell")
    }.await

    refresh("london")
    blockUntilCount(3, "london")
  }

  "a search request of size 0" should "return total count when no query is specified" in {
    val resp = execute {
      search("london").size(0)
    }.await
    assert(3 === resp.totalHits)
  }

  it should "return the document count for the correct type" in {
    val resp = execute {
      search("london" / "landmarks").size(0)
    }.await
    assert(2 === resp.totalHits)
  }

  it should "return the document count based on the specified query" in {
    val resp = execute {
      search("london" / "landmarks").size(0).query("tower")
    }.await
    assert(1 === resp.totalHits)
  }
}
