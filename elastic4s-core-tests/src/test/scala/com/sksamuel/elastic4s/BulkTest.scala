package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.testkit.ElasticSugar

import scala.concurrent.duration._

/** @author Stephen Samuel */
class BulkTest extends FlatSpec with MockitoSugar with ElasticSugar {

  implicit val duration: Duration = 10.seconds

  client.execute {
    index into "transport/air" fields "company" -> "delta" id 99
  }.await

  refresh("transport")
  blockUntilCount(1, "transport")

  "a bulk request" should "execute all index queries" in {

    client.execute(
      bulk(
        index into "transport/air" id 1 fields "company" -> "ba",
        index into "transport/air" id 2 fields "company" -> "aeroflot",
        index into "transport/air" id 3 fields "company" -> "american air",
        index into "transport/air" id 4 fields "company" -> "egypt air"
      )
    ).await
    refresh("transport")
    blockUntilCount(5, "transport", "air")
  }

  "a bulk request" should "execute all delete queries" in {

    client.execute(
      bulk(
        delete(4) from "transport/air",
        delete id 2 from "transport/air"
      )
    ).await
    refresh("transport")
    blockUntilCount(3, "transport", "air")
  }

  "a sync bulk request" should "execute all index queries" in {
    client.execute(
      bulk(
        index into "transport/air" id 5 fields "company" -> "aeromexico",
        index into "transport/air" id 6 fields "company" -> "alaska air",
        index into "transport/air" id 7 fields "company" -> "hawaiian air",
        index into "transport/air" id 8 fields "company" -> "aerotaxi"
      )
    ).await
  }

  "a sync bulk request" should "execute all delete queries" in {
    client.execute(
      bulk(
        delete(8) from "transport/air",
        delete id 5 from "transport/air"
      )
    ).await
  }

}
