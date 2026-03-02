package com.sksamuel.elastic4s.ziohttp

import com.sksamuel.elastic4s.{CommonRequestOptions, ElasticClient, ElasticDsl, ElasticNodeEndpoint, Response}
import com.sksamuel.elastic4s.Authentication.UsernamePassword
import com.sksamuel.elastic4s.testkit.DockerTests.{elasticHost, elasticPort}
import com.sksamuel.elastic4s.zio.instances.taskFunctor
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.http.Client
import zio.{Runtime, Task, TaskLayer, Unsafe, ZIO, ZLayer}

class ZIOHttpClientTest extends AnyFlatSpec with Matchers with ElasticDsl {
  private val zioClientLayer: TaskLayer[ElasticClient[Task]] = Client.default >>> ZLayer.fromZIO {
    ZIO.serviceWith[zio.http.Client](c =>
      ElasticClient[Task](ZIOHttpClient(c, ElasticNodeEndpoint("http", elasticHost, elasticPort.toInt, None)))
    )
  }

  private def runZIO[T](f: ElasticClient[Task] => Task[Response[T]]): Response[T] = {
    Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run(
        ZIO.serviceWithZIO[ElasticClient[Task]](f).provideLayer(zioClientLayer)
      ).getOrThrowFiberFailure()
    }
  }

  "ZIOHttpClient" should "be able to call elasticsearch" in {
    runZIO(_.execute(serverInfo)).result.tagline shouldBe "You Know, for Search"
  }

  it should "be able to propagate auth headers if included" in {
    implicit val options: CommonRequestOptions = CommonRequestOptions.defaults.copy(
      authentication = UsernamePassword("user123", "pass123")
    )

    runZIO(_.execute(catHealth())).result.status shouldBe "401"
  }

  it should "be able index document with id properly" in {
    val id = "id/:/test-id-1"
    runZIO(_.execute(indexInto("testindex2").withId(id))).result.id shouldBe id
  }

  it should "support utf-8" in {
    val id = "я-家"
    runZIO(_.execute(
      indexInto("testindex").withId(id).doc("""{ "text":"¡Hola я 家! ¿Qué tal?" }""")
    )).result.result shouldBe oneOf("created", "updated")
    runZIO(_.execute(indexInto("testindex").withId(id))).result.id shouldBe id
  }

  it should "work with head methods" in {
    runZIO(_.execute(
      indexExists("unknown_index")
    )).result.exists shouldBe false
  }
}
