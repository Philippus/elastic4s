package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import scala.concurrent.duration._

/** @author Stephen Samuel */
class BulkTest extends FlatSpec with MockitoSugar with ElasticSugar {

  implicit val duration: Duration = 10.seconds

  "a bulk request" should "execute all index queries" in {

    client execute(
      index into "transport/air" id 1 fields "company" -> "ba",
      index into "transport/air" id 2 fields "company" -> "aeroflot",
      index into "transport/air" id 3 fields "company" -> "american air",
      index into "transport/air" id 4 fields "company" -> "egypt air"
      )
    refresh("transport")
    blockUntilCount(4, "transport", "air")
  }

  "a bulk request" should "execute all delete queries" in {

    client execute(
      "transport/air" -> 4,
      "transport/air" -> 2
      )
    refresh("transport")
    blockUntilCount(2, "transport", "air")
  }
}
