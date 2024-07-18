package com.sksamuel.elastic4s.sttp

import com.sksamuel.elastic4s.{ElasticClient, ElasticNodeEndpoint, ElasticRequest, Executor, HttpClient, HttpResponse}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SttpRequestHttpClientTest extends AnyFlatSpec with Matchers with DockerTests {
  implicit val executor: Executor[Future] = new Executor[Future] {
    override def exec(client: HttpClient[Future], request: ElasticRequest): Future[HttpResponse] = {
      val cred = Base64.getEncoder.encodeToString("user123:pass123".getBytes(StandardCharsets.UTF_8))
      Executor.FutureExecutor.exec(client, request.copy(headers = Map("Authorization" -> s"Basic $cred")))
    }
  }
  private lazy val sttpClient = SttpRequestHttpClient(ElasticNodeEndpoint("http", elasticHost, elasticPort.toInt, None))
  override lazy val client = ElasticClient(sttpClient)

  "SttpRequestHttpClient" should "propagate headers if included" in {
    client.execute {
      catHealth()
    }.await.result.status shouldBe "401"
  }
}
