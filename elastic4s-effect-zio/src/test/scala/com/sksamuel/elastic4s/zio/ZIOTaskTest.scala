package com.sksamuel.elastic4s.zio

import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.zio.instances._
import org.scalatest.BeforeAndAfterAll
import zio.{DefaultRuntime, Task}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ZIOTaskTest extends AnyFlatSpec with Matchers with DockerTests with BeforeAndAfterAll {

  implicit class RichZIO[A](zio: Task[A]) {
    def unsafeRun: Either[Throwable, A] =
      new DefaultRuntime {}.unsafeRun(zio.either)
  }

  override def beforeAll: Unit = {
    client.execute {
      deleteIndex("testindex")
    }.unsafeRun
  }

  override def afterAll: Unit = {
    client.execute {
      deleteIndex("testindex")
    }.unsafeRun
  }

  it should "index doc successfully" in {
    val r = client.execute {
      indexInto("testindex").doc("""{ "text":"Buna ziua!" }""")
    }.unsafeRun
    r shouldBe 'right
    r.right.get.result.result shouldBe "created"
  }

}

