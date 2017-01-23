package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.testkit.{ElasticMatchers, ElasticSugar}
import org.scalatest.FlatSpec
import org.scalatest.concurrent.Eventually

import scala.concurrent.duration._

class BulkTest extends FlatSpec with ElasticSugar with Eventually with ElasticMatchers {

  override implicit def patienceConfig = PatienceConfig(timeout = 5.seconds)
  implicit val duration: Duration = 10.seconds

  client.execute {
    indexInto("transport/air") fields "company" -> "delta" id 99
  }.await

  refresh("transport")
  blockUntilCount(1, "transport")

  "a bulk request" should "execute all index queries" in {

    client.execute(
      bulk(
        indexInto("transport/air") id 1 fields "company" -> "ba",
        indexInto("transport/air") id 2 fields "company" -> "aeroflot",
        indexInto("transport/air") id 3 fields "company" -> "american air",
        indexInto("transport/air") id 4 fields "company" -> "egypt air"
      )
    ).await

    eventually {
      "transport" should haveCount(5)
    }
  }

  "a bulk request" should "execute all delete queries" in {

    client.execute(
      bulk(
        delete(4) from "transport/air",
        delete id 2 from "transport/air"
      )
    ).await

    eventually {
      "transport" should haveCount(3)
    }
  }
}
