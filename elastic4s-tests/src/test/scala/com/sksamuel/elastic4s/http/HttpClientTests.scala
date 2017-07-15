package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

class HttpClientTests extends FlatSpec with Matchers with DiscoveryLocalNodeProvider with ElasticDsl {

  "DefaultHttpClient" should "support utf8" in {
    http.execute(indexInto("index").doc("""{ "text":"¡Hola! ¿Qué tal?" }""")).await.created shouldBe true
  }
}
