package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DualClientTests
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import org.scalatest.FlatSpec

import scala.util.Try

class CountTest extends FlatSpec with ElasticDsl with DualClientTests {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("london")
      }.await
    }

    execute {
      bulk(
        indexInto("london/landmarks").fields("name" -> "hampton court palace"),
        indexInto("london/landmarks").fields("name" -> "tower of london")
      ).immediateRefresh()
    }.await
  }

  "a search request of size 0" should "return total count when no query is specified" in {
    val resp = execute {
      search("london").size(0)
    }.await
    assert(2 === resp.totalHits)
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
