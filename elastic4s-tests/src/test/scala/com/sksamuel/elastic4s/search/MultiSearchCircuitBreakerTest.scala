package com.sksamuel.elastic4s.search

import scala.util.Try

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MultiSearchCircuitBreakerTest
  extends AnyFlatSpec
    with DockerTests
    with Matchers
    with BeforeAndAfterAll {

  override def beforeAll(): Unit =
    Try {
      client.execute {
        clusterTransientSettings(Map("indices.breaker.total.limit" -> "1b"))
      }.await
    }

  "a multi search request" should "return an error when the circuit breaker is triggered" in {
    val request = multi(search("_all"))
    val response = client.execute(request).await
    response.isError shouldBe true
    response.status shouldEqual 429
    response.error.`type` shouldBe "circuit_breaking_exception"
  }

  override def afterAll(): Unit =
    Try {
      client.execute {
        clusterTransientSettings(Map("indices.breaker.total.limit" -> null))
      }.await
    }
}
