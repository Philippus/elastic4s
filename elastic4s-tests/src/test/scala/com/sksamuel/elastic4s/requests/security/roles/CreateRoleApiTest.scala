package com.sksamuel.elastic4s.requests.security.roles

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.handlers.security.roles.CreateOrUpdateRoleContentBuilder
import org.scalatest.OneInstancePerTest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CreateRoleApiTest extends AnyFlatSpec with JsonSugar with Matchers with OneInstancePerTest {

	"the role dsl" should "generate valid json" in {
		val req = createRole(
			"test_role",
			Seq("other_user"),
			Seq("monitor"),
			Some(GlobalPrivileges(Seq("myapp-*"))),
			Seq(IndexPrivileges(
				Seq("index1", "index2"),
				Seq("read"),
				Some(FieldSecurity(except=Seq("protected_field"))),
				Some("{\"match\": {\"category\": \"testing\"}}")
			)),
			Seq(ApplicationPrivileges(
				"myapp",
				Seq("data:read/*")
			))
		)
		CreateOrUpdateRoleContentBuilder(req).string should matchJsonResource("/json/createrole/createrole.json")
	}
}
