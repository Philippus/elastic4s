package com.sksamuel.elastic4s2

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.FlatSpec
import org.scalatest.concurrent.Eventually
import com.sksamuel.elastic4s.testkit.{ElasticMatchers, ElasticSugar}

import scala.concurrent.duration._

/** @author Stephen Samuel */
class BulkTest extends FlatSpec with ElasticSugar with Eventually with ElasticMatchers {

  override implicit def patienceConfig = PatienceConfig(timeout = 5.seconds)
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
