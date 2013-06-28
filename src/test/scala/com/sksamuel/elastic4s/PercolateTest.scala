package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import PercolateDsl._
import scala.concurrent.duration._

/** @author Stephen Samuel */
class PercolateTest extends FlatSpec with MockitoSugar with ElasticSugar {

    implicit val duration = 10.seconds

    client.register {
        "assam" into "tea" query "assam"
    }
    client.register {
        "earlgray" into "tea" query "earl gray"
    }
    refresh("_percolate")
    blockUntilCount(2, "tea")

    "a percolate request" should "return queries that match the document" in {

        val resp = client result {
            percolate in "tea" fields "name" -> "assam"
        }
        assert(1 === resp.getMatches.size)
        assert(1 === resp.getMatches.get(0))
    }
}
