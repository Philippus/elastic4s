package com.sksamuel.elastic4s.requests.security.roles

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}
import scala.util.Try

class GetRoleTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteRole("role1")
    }.await
  }

  Try {
    client.execute {
      deleteRole("role2")
    }.await
  }

  client.execute {
    createRole(
      "role1",
      indices=Seq(IndexPrivileges(
        Seq("index1", "index2"),
        Seq("read"),
        allow_restricted_indices=Some(false)
      ))
    )
  }.await

  client.execute {
    createRole("role2")
  }.await

  "get role" should {
    "return role info" in {
      val resp = client.execute {
        getRole("role1")
      }.await.result

      resp.size shouldBe 1
      resp("role1").indices shouldBe Seq(IndexPrivileges(
        Seq("index1","index2"),
        Seq("read"),
        allow_restricted_indices=Some(false)
      ))
    }
  }

  "get roles" should {
    "return all roles info" in {
      val resp = client.execute {
        getRoles()
      }.await.result

      resp.size >= 2 shouldBe true
      resp.contains("role1") shouldBe true
      resp.contains("role2") shouldBe true
    }
  }
}
