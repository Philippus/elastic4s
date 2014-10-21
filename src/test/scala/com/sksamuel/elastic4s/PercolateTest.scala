package com.sksamuel.elastic4s

import org.scalatest.mock.MockitoSugar
import org.scalatest.{ FlatSpec, Matchers }

/** @author Stephen Samuel */
class PercolateTest extends FlatSpec with Matchers with MockitoSugar with ElasticSugar {

  // todo re-enable with 1.4.0 final
  //  client.execute {
  //    create index "teas" shards 1
  //  }.await
  //
  //  client.execute {
  //    "a" into "teas" query {
  //      term("flavour", "assam")
  //    }
  //  }.await
  //
  //  client.execute {
  //    "b" into "teas" query {
  //      term("flavour", "earl")
  //    }
  //  }.await
  //
  //  client.execute {
  //    "c" into "teas" query {
  //      term("flavour", "darjeeling")
  //    }
  //  }.await

  // todo re-enable with 1.4.0 final
  //  "a percolate request" should "return queries that match the document" in {
  //
  //    val matches = client.execute {
  //      "teas" doc "flavour" -> "assam"
  //    }.await.getMatches
  //
  //    matches.size shouldBe 1
  //    matches(0).getId.string shouldBe "a"
  //  }
}
