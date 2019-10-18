package com.sksamuel.elastic4s.requests.security.users

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}
import scala.util.Try

class GetUserTest extends WordSpec with Matchers with DockerTests {

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
    createUser(
      "user1",
      password=PlaintextPassword("test1234")
    )
  }.await

  client.execute {
    createUser(
      "user2",
      password=PlaintextPassword("test1234")
    )
  }.await

  "get role" should {
    "return user info" in {
      val resp = client.execute {
        getUser("user1")
      }.await.result

      resp.size shouldBe 1
      resp("user1").roles shouldBe Seq()
      resp("user1").enabled shouldBe true
    }
  }

  "get users" should {
    "return all users info" in {
      val resp = client.execute {
        getUsers()
      }.await.result

      resp.size >= 2 shouldBe true
      resp.contains("user1") shouldBe true
      resp.contains("user2") shouldBe true
    }
  }
}
