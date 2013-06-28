package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import IndexDsl._
import scala.concurrent.duration._

/** @author Stephen Samuel */
class BulkTest extends FlatSpec with MockitoSugar with ElasticSugar {

    implicit val duration: Duration = 10.seconds

    "a bulk rqeuest" should "execute all index queries" in {

        client result (
          index into "transport/air" fields "company" -> "ba",
          index into "transport/air" fields "company" -> "aeroflot",
          index into "transport/air" fields "company" -> "american air",
          index into "transport/air" fields "company" -> "egypt air"
          )
        refresh("transport")
        blockUntilCount(4, "transport", "air")
    }
}
