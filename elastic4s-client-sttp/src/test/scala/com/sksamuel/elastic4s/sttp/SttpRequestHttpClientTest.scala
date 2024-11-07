package com.sksamuel.elastic4s.sttp

import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.{Authentication, CommonRequestOptions, ElasticClient, ElasticNodeEndpoint}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SttpRequestHttpClientTest extends AnyFlatSpec with Matchers with DockerTests {
  private lazy val sttpClient = SttpRequestHttpClient(ElasticNodeEndpoint("http", elasticHost, elasticPort.toInt, None))
  override val client = ElasticClient(sttpClient)

  "SttpRequestHttpClient" should "propagate headers if included" in {
    implicit val options: CommonRequestOptions = CommonRequestOptions.defaults.copy(
      authentication = Authentication.UsernamePassword("user123", "pass123")
    )

    client.execute {
      catHealth()
    }.await.result.status shouldBe "401"
  }
}
