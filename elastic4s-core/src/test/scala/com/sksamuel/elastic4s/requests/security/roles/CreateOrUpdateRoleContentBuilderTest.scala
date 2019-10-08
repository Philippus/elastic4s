package com.sksamuel.elastic4s.requests.security.roles

import org.scalatest.{FunSuite, Matchers}

class CreateOrUpdateRoleContentBuilderTest extends FunSuite with Matchers {
	test("CreateOrUpdateRoleContentBuilder should generate the correct json") {
		// Example taken from https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-put-role.html#security-api-put-role-example
		val create = CreateOrUpdateRoleRequest(
			"my_admin_role",
			CreateRole,
			Seq("other_user"),
			Seq("all"),
			indices=Seq(IndexPrivileges(
				Seq("index1", "index2"),
				Seq("all"),
				Some(FieldSecurity(Seq("title","body"))),
				Some("{\"match\":{\"title\":\"foo\"}}")
			)),
			applications=Seq(ApplicationPrivileges(
				"myapp",
				Seq("admin","read"),
				Seq("*")
			))
		)
		val expected = """
		{
		  "run_as": [ "other_user" ],
		  "cluster": ["all"],
		  "indices": [{
	      "names": [ "index1", "index2" ],
	      "privileges": ["all"],
	      "field_security" : {
	        "grant" : [ "title", "body" ]
	      },
	      "query": "{\"match\": {\"title\": \"foo\"}}"
		  }],
		  "applications": [
		    {
		      "application": "myapp",
		      "privileges": [ "admin", "read" ],
		      "resources": [ "*" ]
		    }
		  ]
		}
		""".filterNot(c => c.isWhitespace)

		val result = CreateOrUpdateRoleContentBuilder(create).string
		result shouldBe expected
	}
}
