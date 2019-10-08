package com.sksamuel.elastic4s.requests.security.users

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}
import scala.util.Try

class EnnableUserTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteUser("user1")
    }.await
  }

  Try {
    client.execute {
      deleteUser("user2")
    }.await
  }

  client.execute {
    createUser("user1", password=PlaintextPassword("test1234"), enabled=Some(false))
  }.await

  "disable user request" should {
    "return nothing" in {
      noException should be thrownBy client.execute {
        enableUser("user1")
      }.await

      val resp = client.execute {
        getUser("user1")
      }.await

      resp.result("user1").enabled shouldBe true
    }

    "return error if the user does not exist" in {
      val resp = client.execute{
        enableUser("user2")
      }.await

      resp.error.`type` shouldBe "validation_exception"
    }
  }
}
