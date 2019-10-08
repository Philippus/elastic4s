package com.sksamuel.elastic4s.requests.security.users

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.JsonSugar
import org.scalatest.{FlatSpec, Matchers, OneInstancePerTest}

class CreateUserApiTest extends FlatSpec with JsonSugar with Matchers with OneInstancePerTest {

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
		CreateOrUpdateUserContentBuilder(req).string() should matchJsonResource("/json/createUser/createuser.json")
	}

	it should "handle updating users" in {
		val req = updateUser(
			"test_user",
			enabled=Some(false),
			metadata=Map("age" -> 100)
		)
		CreateOrUpdateUserContentBuilder(req).string() should matchJsonResource("/json/createUser/updateuser.json")
	}
}