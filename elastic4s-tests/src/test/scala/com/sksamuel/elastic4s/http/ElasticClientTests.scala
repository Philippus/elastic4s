package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.{ElasticRequest, Executor, HttpClient, HttpResponse}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.concurrent.Future
import scala.util.Try

class ElasticClientTests extends AnyFlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("testindex")
    }.await
  }

  "DefaultHttpClient" should "support utf-8" in {
    client.execute {
      indexInto("testindex").doc("""{ "text":"¡Hola! ¿Qué tal?" }""")
    }.await.result.result shouldBe "created"
  }

  it should "propagate headers if included" in {
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
