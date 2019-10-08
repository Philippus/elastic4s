package com.sksamuel.elastic4s.requests.security.users

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}
import scala.util.Try

class ChangeUserPasswordTest extends WordSpec with Matchers with DockerTests {

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

  "change password request" should {
    "return nothing" in {

      client.execute {
        createUser("user1", password=PlaintextPassword("test1234"))
      }.await

      noException should be thrownBy client.execute {
        changePassword("user1", "9876test")
      }.await
    }

    "return error if the user does not exist" in {
      val resp = client.execute{
        changePassword("user2", "1234test")
      }.await

      resp.error.`type` shouldBe "validation_exception"
    }
  }
}
