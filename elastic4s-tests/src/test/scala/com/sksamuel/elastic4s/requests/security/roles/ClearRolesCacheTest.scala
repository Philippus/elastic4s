package com.sksamuel.elastic4s.requests.security.roles

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

class ClearRolesCacheTest extends WordSpec with Matchers with DockerTests {

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
