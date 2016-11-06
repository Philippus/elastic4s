package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

class MultiSearchTest
    extends FlatSpec
    with ElasticSugar
    with Matchers
    with ScalaFutures {

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(timeout = 10.seconds, interval = 1.seconds)

  "a multi search request" should "find matching documents for all queries" in {

    client.execute {
      createIndex("jtull")
    }.await

    val futureInsert1 = client.execute {
      index into "jtull/albums" fields ("name" -> "aqualung") id 14
    }

    val futureInsert2 = client.execute {
      index into "jtull/albums" fields ("name" -> "passion play") id 51
    }

    val futureInserts = for {
      insert1 <- futureInsert1
      insert2 <- futureInsert2
    } yield blockUntilCount(2, "jtull")

    val futureResponse = futureInserts flatMap { _ =>
      client execute {
        multi(
          search("jtull/albums") query "aqualung",
          search("jtull/albums") query "passion"
        )
      }
    }

    whenReady(futureResponse) { response =>
      response.responses.size shouldBe 2
      response.size shouldBe 2
      response.responses.head.response.hits.head.id shouldBe "14"
      response.responses.tail.head.response.hits.head.id shouldBe "51"
    }
  }
}
