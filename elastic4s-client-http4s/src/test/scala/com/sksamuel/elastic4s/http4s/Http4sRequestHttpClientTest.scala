package com.sksamuel.elastic4s.http4s

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.{Authentication, CommonRequestOptions, ElasticClient, ElasticNodeEndpoint}
import org.http4s.ember.client.EmberClientBuilder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Http4sRequestHttpClientTest extends AnyFlatSpec with Matchers with DockerTests {
  private val http4s = EmberClientBuilder.default[IO].build.allocated.unsafeRunSync()._1
  private val http4sClient = Http4sClient.usingIO(
    http4s,
    ElasticNodeEndpoint("http", elasticHost, elasticPort.toInt, None),
  )
  override val client: ElasticClient = ElasticClient(http4sClient)

  "Http4sRequestHttpClient" should "be able to call elasticsearch" in {
    client.execute {
      serverInfo
    }.await.result.tagline shouldBe "You Know, for Search"
  }

  it should "be able to propagate headers if included" in {
    implicit val options: CommonRequestOptions = CommonRequestOptions.defaults.copy(
      authentication = Authentication.UsernamePassword("user123", "pass123")
    )

    client.execute {
      catHealth()
    }.await.result.status shouldBe "401"
  }

}
