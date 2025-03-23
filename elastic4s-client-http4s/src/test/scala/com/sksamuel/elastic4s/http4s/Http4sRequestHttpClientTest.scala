package com.sksamuel.elastic4s.http4s

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.testkit.DockerTests.{elasticHost, elasticPort}
import com.sksamuel.elastic4s.{Authentication, CommonRequestOptions, ElasticClient}
import org.http4s.ember.client.EmberClientBuilder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Http4sRequestHttpClientTest extends AnyFlatSpec with Matchers with DockerTests {
  private val emberClient  = EmberClientBuilder.default[IO].build.allocated.unsafeRunSync()._1
  private val http4sClient = new Http4sClient(
    emberClient,
    org.http4s.Uri.unsafeFromString(s"http://$elasticHost:$elasticPort")
  )

  val ioClient: ElasticClient[IO] = new ElasticClient[IO](http4sClient)

  "Http4sRequestHttpClient" should "be able to call elasticsearch" in {
    ioClient.execute {
      serverInfo
    }.unsafeRunSync().result.tagline shouldBe "You Know, for Search"
  }

  it should "be able to propagate headers if included" in {
    implicit val options: CommonRequestOptions = CommonRequestOptions.defaults.copy(
      authentication = Authentication.UsernamePassword("user123", "pass123")
    )

    ioClient.execute {
      catHealth()
    }.unsafeRunSync().result.status shouldBe "401"
  }

}
