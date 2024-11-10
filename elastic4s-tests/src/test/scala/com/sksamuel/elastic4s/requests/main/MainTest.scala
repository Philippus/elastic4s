package com.sksamuel.elastic4s.requests.main

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MainTest extends AnyFlatSpec with Matchers with ElasticDsl with DockerTests {
  "main" should "return the tagline" in {
    val resp = client.execute {
      serverInfo
    }.await.result

    resp.tagline shouldBe "You Know, for Search"
  }
}
