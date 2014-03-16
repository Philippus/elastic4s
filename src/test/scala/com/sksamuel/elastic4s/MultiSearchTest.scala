package com.sksamuel.elastic4s

import org.scalatest.{ FlatSpec, Matchers, OneInstancePerTest }
import org.scalatest.mock.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.common.Priority
import scala.concurrent.ExecutionContext.Implicits.global

/** @author Stephen Samuel */
class MultiSearchTest
    extends FlatSpec
    with ElasticSugar
    with Matchers
    with OneInstancePerTest
    with ScalaFutures {

  override implicit def patienceConfig = PatienceConfig(timeout = 10 seconds, interval = 1 seconds)

  "a multi search request" should "find matching documents for all queries" in {

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
      client search (
        search in "jtull/albums" query "aqualung",
        search in "jtull/albums" query "passion"
      )
    }

    whenReady(futureResponse) { response =>
      response.getResponses.size shouldBe 2
      response.getResponses()(0).getResponse.getHits.getAt(0).id() shouldBe "14"
      response.getResponses()(1).getResponse.getHits.getAt(0).id() shouldBe "51"
    }
  }
}
