package com.sksamuel.elastic4s.requests.security.roles

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ClearRolesCacheTest extends AnyWordSpec with Matchers with DockerTests {

  "clear roles cache request" should {
    "return ack" in {

      client.execute {
        createRole("role1")
      }.await

      val resp = client.execute {
        clearRolesCache("role1")
      }.await

      resp.result._nodes.successful > 0 shouldBe true
    }
  }
}
