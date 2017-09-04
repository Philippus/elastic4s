package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class HttpClientTests extends FlatSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("testindex")
    }.await
  }

  "DefaultHttpClient" should "support utf8" in {
    http.execute(indexInto("testindex").doc("""{ "text":"¡Hola! ¿Qué tal?" }""")).await.right.get.result shouldBe "created"
  }
}
