package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import PercolateDsl._
import CreateIndexDsl._
import scala.concurrent.duration._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class PercolateTest extends FlatSpec with MockitoSugar with ElasticSugar {

    implicit val duration = 10.seconds

    client execute {
        create index "teas" shards 1
    }

    client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

    client register {
        "a" into "teas" query {
            term("flavour", "assam")
        }
    }
    client register {
        "b" into "teas" query {
            term("flavour", "earl")
        }
    }
    client register {
        "c" into "teas" query {
            term("flavour", "darjeeling")
        }
    }
    refresh("teas")
    refresh("_percolator")
    client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet
    blockUntilCount(3, "_percolator", "teas")

    "a percolate request" should "return queries that match the document" in {

        val resp = client.sync.percolate {
            "teas" doc "flavour" -> "assam"
        }
        assert(1 === resp.getMatches.size)
        assert("a" === resp.getMatches.get(0))
    }
}
