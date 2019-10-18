package com.sksamuel.elastic4s.requests.security.users

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

class DeleteUserTest extends WordSpec with Matchers with DockerTests {

  "delete user request" should {
    "delete user" in {

      client.execute {
        createUser("user1", password=PlaintextPassword("test1234"))
      }.await

      client.execute {
        getUser("user1")
      }.await.result.contains("user1") shouldBe true

      client.execute {
        deleteUser("user1")
      }.await.result.found shouldBe true

      client.execute {
        getUser("user1")
      }.await.error.`type` shouldBe "404"
    }

    "do nothing if the user does not exist" in {
      client.execute {
        createUser("user1", password=PlaintextPassword("test1234"))
      }.await

      client.execute {
        deleteUser("user1")
      }.await.result.found shouldBe true

      client.execute {
        deleteUser("user1")
      }.await.result.found shouldBe false
    }
  }
}
