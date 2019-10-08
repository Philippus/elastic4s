package com.sksamuel.elastic4s.requests.security.roles

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

class DeleteRoleTest extends WordSpec with Matchers with DockerTests {

  "delete role request" should {
    "delete role" in {

      client.execute {
        createRole("role1")
      }.await

      client.execute {
        getRole("role1")
      }.await.result.contains("role1") shouldBe true

      client.execute {
        deleteRole("role1")
      }.await.result.found shouldBe true

      client.execute {
        getRole("role1")
      }.await.error.`type` shouldBe "404"
    }
  }
}
