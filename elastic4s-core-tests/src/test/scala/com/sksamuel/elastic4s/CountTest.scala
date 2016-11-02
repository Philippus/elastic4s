package com.sksamuel.elastic4s2

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.testkit.ElasticSugar

/** @author Stephen Samuel */
class CountTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    index into "london/landmarks" fields "name" -> "hampton court palace"
  }.await

  client.execute {
    index into "london/landmarks" fields "name" -> "tower of london"
  }.await

  client.execute {
    index into "london/pubs" fields "name" -> "blue bell"
  }.await

  refresh("london")
  blockUntilCount(3, "london")

  "a count request" should "return total count when no query is specified" in {
    val resp = client.execute {
      count from "london"
    }.await
    assert(3 === resp.getCount)
  }

  "a count request" should "return the document count for the correct type" in {
    val resp = client.execute {
      count from "london" -> "landmarks"
    }.await
    assert(2 === resp.getCount)
  }

  // todo looks like elasticsearch bug
  //  "a count request" should "return the document count based on the specified query" in {
  //    val resp = client.sync.execute {
  //     count from "london" -> "landmarks" query "tower"
  //  }
  //    assert(1 === resp.getCount)
  //  }
}
