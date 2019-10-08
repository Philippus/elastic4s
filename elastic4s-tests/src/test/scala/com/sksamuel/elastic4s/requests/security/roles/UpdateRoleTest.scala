package com.sksamuel.elastic4s.requests.security.roles

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}
import scala.util.Try

class UpdateRoleTest extends WordSpec with Matchers with DockerTests {
	
	Try {
		client.execute {
			deleteRole("role1")
		}.await
	}

	client.execute {
		createRole("role1")
	}.await

	"UpdateRole Http Request" should {
		"update the role" in {
			val indexPrivileges = Seq(IndexPrivileges(
				Seq("index1", "index2"),
				Seq("read"),
				allow_restricted_indices=Some(false)
			))
			val resp = client.execute {
				updateRole(
					"role1",
					indices=indexPrivileges
				)
			}.await

			val roles = client.execute {
				getRole("role1")
			}.await

			resp.result.role.created shouldBe false
			roles.result.contains("role1") shouldBe true
			roles.result("role1").indices shouldBe indexPrivileges
		}
	}
}