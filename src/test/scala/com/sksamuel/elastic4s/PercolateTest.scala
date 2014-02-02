package com.sksamuel.elastic4s

import org.scalatest.{ FlatSpec, Matchers }
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import scala.concurrent.duration._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class PercolateTest extends FlatSpec with Matchers with MockitoSugar with ElasticSugar {

  client.sync.execute {
    create index "teas" shards 1
  }

  client execute {
    "a" into "teas" query {
      term("flavour", "assam")
    }
  }
  client execute {
    "b" into "teas" query {
      term("flavour", "earl")
    }
  }
  client.sync.execute {
    "c" into "teas" query {
      term("flavour", "darjeeling")
    }
  }

  "a percolate request" should "return queries that match the document" in {

    val matches = client.sync.execute {
     "teas" doc "flavour" -> "assam"
    } getMatches

    matches.size shouldBe 1
    matches(0).getId.string shouldBe "a"
  }
}
