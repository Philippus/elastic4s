package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class HttpClientTests extends FlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("testindex")
    }.await
  }

  "DefaultHttpClient" should "support utf-8" in {
    client.execute {
      indexInto("testindex" / "testindex").doc("""{ "text":"¡Hola! ¿Qué tal?" }""")
    }.await.right.get.result.result shouldBe "created"
  }
}
