package com.sksamuel.elastic4s.explain

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.{ElasticMatchers, ElasticSugar}
import org.scalatest.FlatSpec
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.SpanSugar._

class ExplainTest
  extends FlatSpec
  with ElasticSugar
  with Eventually
  with ElasticMatchers
  with ScalaFutures {

  override implicit def patienceConfig = PatienceConfig(timeout = 10 seconds, interval = 1 seconds)

  client.execute {
    index into "queens/england" fields ("name" -> "qe2") id 8
  }.await

  refresh("queens")
  blockUntilCount(1, "queens")

  "an explain request" should "explain a matching document" in {

    val f = client.execute {
      explain("queens", "england", "8") query termQuery("name", "qe2")
    }

    whenReady(f) { response =>
      response.getGetResult
      response.isMatch shouldBe true
    }
  }

  it should "explain a not matching document" in {

    val f = client.execute {
      explain("queens", "england", "24") query termQuery("name", "qe2")
    }

    whenReady(f) { response =>
      response.isMatch shouldBe false
    }
  }
}
