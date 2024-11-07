package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.{Authentication, CommonRequestOptions}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

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
    implicit val requestOptions: CommonRequestOptions = CommonRequestOptions.defaults.copy(
      authentication = Authentication.UsernamePassword("user123", "pass123")
    )

    client.execute {
      catHealth()
    }.await.result.status shouldBe "401"
  }
}
