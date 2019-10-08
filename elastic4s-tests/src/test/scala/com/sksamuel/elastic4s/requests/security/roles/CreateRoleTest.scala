package com.sksamuel.elastic4s.requests.security.roles

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}
import scala.util.Try

class CreateRoleTest extends WordSpec with Matchers with DockerTests {
	
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
		createRole("role2")
	}.await

	"CreateRole Http Request" should {
		"return ack" in {
			val resp = client.execute {
				createRole(
					"role1",
					indices=Seq(IndexPrivileges(
						Seq("index1", "index2"),
						Seq("read")
					))
				)
			}.await

			resp.result.role.created shouldBe true
		}

		"return false if role already exists" in {
			val resp = client.execute {
				createRole("role2")
			}.await

			resp.result.role.created shouldBe false
		}
	}
}