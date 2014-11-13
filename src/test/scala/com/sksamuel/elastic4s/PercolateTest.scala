package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.scalatest.mock.MockitoSugar
import org.scalatest.{ FlatSpec, Matchers }
import com.sksamuel.elastic4s.ElasticDsl._

/** @author Stephen Samuel */
class PercolateTest extends FlatSpec with Matchers with MockitoSugar with ElasticSugar {

  //  todo re-enable with 1.4.0 final
  client.execute {
    create index "percolate" mappings {
      "teas" as {
        "flavour" typed StringType
      }
    }
  }.await

  client.execute {
    "a" into "percolate" query {
      termQuery("flavour", "assam")
    }
  }.await

  client.execute {
    "b" into "percolate" query {
      termQuery("flavour", "earl")
    }
  }.await

  client.execute {
    "c" into "percolate" query {
      termQuery("flavour", "darjeeling")
    }
  }.await

  //todo re-enable with 1.4.0 final
  "a percolate request" should "return queries that match the document" in {

    val matches = client.execute {
      "percolate/teas" doc "flavour" -> "assam"
    }.await.getMatches

    matches.size shouldBe 1
    matches(0).getId.string shouldBe "a"
  }
}
