package com.sksamuel.elastic4s.requests.explain

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class ExplainTest extends FlatSpec with Matchers with ElasticDsl with DockerTests {

  Try {
    client.execute {
      deleteIndex("explain")
    }.await
  }

  client.execute {
    bulk(
      indexInto("explain/kings") fields ("name" -> "richard") id "4",
      indexInto("explain/kings") fields ("name" -> "edward") id "5"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "an explain request" should "explain a matching document" in {
    val resp = client.execute {
      explain("explain", "kings", "4") query termQuery("name", "richard")
    }.await.result

    resp.isMatch shouldBe true
  }

  it should "not explain a not found document" in {
    client.execute {
      explain("explain", "kings", "24") query termQuery("name", "edward")
    }.await.result.isMatch shouldBe false
  }
}
