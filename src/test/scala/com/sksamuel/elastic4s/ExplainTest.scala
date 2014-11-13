package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import org.scalatest.{ FlatSpec, Matchers, OneInstancePerTest }

class ExplainTest
    extends FlatSpec
    with ElasticSugar
    with Matchers
    with OneInstancePerTest
    with ScalaFutures {

  override implicit def patienceConfig: ExplainTest.this.type#PatienceConfig = PatienceConfig(timeout = 10 seconds, interval = 1 seconds)

  client.execute {
    index into "queens/england" fields ("name" -> "qe2") id 8
  }.await

  refresh("queens")
  blockUntilCount(1, "queens")

  "an explain request" should "explain a matching document" in {

    val response = client.execute {
      explain id 8 in "queens/england" query termQuery("name", "qe2")
    }.await

    response.isMatch shouldBe true

    val futureResponse = client.execute {
      explain id 8 in "queens/england" query termQuery("name", "qe2")
    }

    whenReady(futureResponse) { response =>
      response.isMatch shouldBe true
    }
  }

  it should "explain a not matching document" in {

    val response = client.execute {
      explain id 24 in "queens/england" query termQuery("name", "qe2")
    }.await

    response.isMatch shouldBe false

    val futureResponse = client.execute {
      explain id 24 in "queens/england" query termQuery("name", "qe2")
    }

    whenReady(futureResponse) { response =>
      response.isMatch shouldBe false
    }
  }
}
