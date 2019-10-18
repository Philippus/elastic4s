package com.sksamuel.elastic4s.requests.security.users

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}
import scala.util.Try

class CreateUserTest extends WordSpec with Matchers with DockerTests {
	
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
		createUser("user2", password=PlaintextPassword("testing"))
	}

	"CreateUser Http Request" should {
		"return ack" in {
			val resp = client.execute {
				createUser(
					"user1",
					password=PlaintextPassword("testpassword"),
					roles=Seq("role1", "role2")
				)
			}.await

			resp.result.created shouldBe true
		}

		"return false if user already exists" in {
			val resp = client.execute {
				createUser("user2", password=PlaintextPassword("testing"))
			}.await

			resp.result.created shouldBe false
		}
	}
}