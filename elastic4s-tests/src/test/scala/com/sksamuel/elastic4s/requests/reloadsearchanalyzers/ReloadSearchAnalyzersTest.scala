package com.sksamuel.elastic4s.requests.reloadsearchanalyzers

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReloadSearchAnalyzersTest extends AnyFlatSpec with Matchers with ElasticDsl with DockerTests {
  "reload search analyzers request" should "return a success" in {
    val resp = client.execute {
      reloadSearchAnalyzers("*")
    }.await.result

    resp.reloadDetails.map(_.reloadedNodeIds).nonEmpty shouldBe true
  }
}
