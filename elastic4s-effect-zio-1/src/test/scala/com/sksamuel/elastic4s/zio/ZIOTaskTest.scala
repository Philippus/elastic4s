package com.sksamuel.elastic4s.zio

import com.sksamuel.elastic4s.{ElasticClient, ElasticNodeEndpoint}
import com.sksamuel.elastic4s.sttp.SttpRequestHttpClient
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.zio.instances._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.SttpBackend
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.impl.zio.RIOMonadAsyncError
import sttp.monad.MonadError
import zio.Task

class ZIOTaskTest extends AnyFlatSpec with Matchers with DockerTests with BeforeAndAfterAll {
  private implicit val sttpZioMonadError: MonadError[Task] = new RIOMonadAsyncError()
  private implicit val sttpBackend: SttpBackend[Task, Any] =
    HttpClientZioBackend().unsafeRun.fold(throw _, identity)

  private lazy val sttpClient: SttpRequestHttpClient[Task] =
    new SttpRequestHttpClient(ElasticNodeEndpoint("http", elasticHost, elasticPort.toInt, None))

  val zioClient: ElasticClient[Task] = ElasticClient(sttpClient)

  implicit class RichZIO[A](task: Task[A]) {
    def unsafeRun: Either[Throwable, A] = {
      zio.Runtime.default.unsafeRun(task.either)
    }
  }

  override def beforeAll(): Unit = {
    zioClient.execute {
      deleteIndex("testindex")
    }.unsafeRun
  }

  override def afterAll(): Unit = {
    zioClient.execute {
      deleteIndex("testindex")
    }.unsafeRun
  }

  it should "index doc successfully" in {
    val r = zioClient.execute {
      indexInto("testindex").doc("""{ "text":"Buna ziua!" }""")
    }.unsafeRun
    r shouldBe Symbol("right")
    r.right.get.result.result shouldBe "created"
  }

}

