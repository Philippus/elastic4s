package com.sksamuel.elastic4s

import org.scalatest.{ FlatSpec, Matchers, OneInstancePerTest }
import org.scalatest.mock.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.common.Priority
import scala.concurrent.ExecutionContext.Implicits.global

class ExplainTest
    extends FlatSpec
    with ElasticSugar
    with Matchers
    with OneInstancePerTest
    with ScalaFutures {

  override implicit def patienceConfig = PatienceConfig(timeout = 10 seconds, interval = 1 seconds)

  client.sync.execute {
    index into "beer/lager" fields ("name" -> "budweiser") id 8
  }

  refresh("beer")

  "an explain request" should "explain a matching document" in {

    val response = client.sync.execute {
      explain id 8 in "beer/lager" query termQuery("name", "budweiser")
    }

    response.isMatch shouldBe true

    val futureResponse = client.execute {
      explain id 8 in "beer/lager" query termQuery("name", "budweiser")
    }

    whenReady(futureResponse) { response =>
      response.isMatch shouldBe true
    }
  }

  "an explain request" should "explain a not matching document" in {

    val response = client.sync.execute {
      explain id 24 in "beer/lager" query termQuery("name", "budweiser")
    }

    response.isMatch shouldBe false

    val futureResponse = client.execute {
      explain id 24 in "beer/lager" query termQuery("name", "budweiser")
    }

    whenReady(futureResponse) { response =>
      response.isMatch shouldBe false
    }
  }
}
