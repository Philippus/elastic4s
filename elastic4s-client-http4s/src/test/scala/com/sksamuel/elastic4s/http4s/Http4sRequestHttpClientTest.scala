package com.sksamuel.elastic4s.http4s

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.sksamuel.elastic4s.{ElasticClient, ElasticNodeEndpoint, ElasticRequest, Executor, HttpClient, HttpResponse}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.http4s.ember.client.EmberClientBuilder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.concurrent.Future

class Http4sRequestHttpClientTest extends AnyFlatSpec with Matchers with DockerTests {
  private val http4s = EmberClientBuilder.default[IO].build.allocated.unsafeRunSync()._1
  private val http4sClient = Http4sClient.usingIO(
    http4s,
    ElasticNodeEndpoint("http", elasticHost, elasticPort.toInt, None),
    Authentication.NoAuth
  )
  override val client: ElasticClient = ElasticClient(http4sClient)

  "Http4sRequestHttpClient" should "be able to call elasticsearch" in {
    client.execute {
      catHealth()
    }.await.result.status shouldBe "green"
  }

  it should "be able to propagate headers if included" in {
    implicit val executor: Executor[Future] = new Executor[Future] {
      override def exec(client: HttpClient, request: ElasticRequest): Future[HttpResponse] = {
        val cred = Base64.getEncoder.encodeToString("user123:pass123".getBytes(StandardCharsets.UTF_8))
        Executor.FutureExecutor.exec(client, request.copy(headers = Map("Authorization" -> s"Basic $cred")))
      }
    }

    client.execute {
      catHealth()
    }.await.result.status shouldBe "401"
  }

}
