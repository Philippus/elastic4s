package com.sksamuel.elastic4s.requests.security.users

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.handlers.security.users
import com.sksamuel.elastic4s.handlers.security.users.CreateOrUpdateUserContentBuilder
import org.scalatest.OneInstancePerTest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CreateUserApiTest extends AnyFlatSpec with JsonSugar with Matchers with OneInstancePerTest {

	"the user dsl" should "generate valid json" in {
		val req = createUser(
			"test_user",
			enabled=Some(true),
			email=Some("user@test.com"),
			fullName=Some("Test User"),
			metadata=Map("age" -> 99, "occupation" -> "testing"),
			password=PlaintextPassword("test1234"),
			roles=Seq("role1", "role2")
		)
		CreateOrUpdateUserContentBuilder(req).string should matchJsonResource("/json/createuser/createuser.json")
	}

	it should "handle updating users" in {
		val req = updateUser(
			"test_user",
			enabled=Some(false),
			metadata=Map("age" -> 100)
		)
		users.CreateOrUpdateUserContentBuilder(req).string should matchJsonResource("/json/createuser/updateuser.json")
	}
}
