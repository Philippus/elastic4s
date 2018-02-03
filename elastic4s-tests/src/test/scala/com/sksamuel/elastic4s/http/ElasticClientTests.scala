package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class ElasticClientTests extends FlatSpec with Matchers with DockerTests {

  Try {
    http.execute {
      deleteIndex("testindex")
    }.await
  }

  "DefaultHttpClient" should "support utf-8" in {
    http.execute {
      indexInto("testindex" / "testindex").doc("""{ "text":"¡Hola! ¿Qué tal?" }""")
    }.await.result.result shouldBe "created"
  }
}
