package com.sksamuel.elastic4s.requests.security.users

import org.scalatest.{FunSuite, Matchers}

class CreateOrUpdateUserContentBuilderTest extends FunSuite with Matchers {
	val spaceBetweenPunctuation = "([:\\{]) *([a-z\"\\[\\{])".r

	test("CreateOrUpdateUserContentBuilder should generate the correct json") {
		val create = CreateOrUpdateUserRequest(
			"test-user",
			CreateUser,
			fullName=Some("Test User"),
			password=Some(PlaintextPassword("password123")),
			roles=Seq("test-role")
		)
		val expectedJson = """
		{
			"full_name": "Test User",
			"password": "password123",
			"roles": ["test-role"]
		}
		""".replace("\n", "").replace("\t", "")
		val formattedJson = spaceBetweenPunctuation.replaceAllIn(expectedJson, "$1$2")
		val result = CreateOrUpdateUserContentBuilder(create).string 
		result shouldBe formattedJson
	}

	test("CreateOrUpdateUserContentBuilder should handle a password hash") {
		val create = CreateOrUpdateUserRequest(
			"test-user",
			CreateUser,
			fullName=Some("Test User"),
			password=Some(PasswordHash("password123")),
			roles=Seq("test-role")
		)
		val expectedJson = """
		{
			"full_name": "Test User",
			"password_hash": "password123",
			"roles": ["test-role"]
		}
		""".replace("\n", "").replace("\t", "")
		val formattedJson = spaceBetweenPunctuation.replaceAllIn(expectedJson, "$1$2")
		val result = CreateOrUpdateUserContentBuilder(create).string 
		result shouldBe formattedJson
	}

	test("CreateOrUpdateUserContentBuilder should handle all optional fields") {
		val create = CreateOrUpdateUserRequest(
			"test-user",
			CreateUser,
			enabled=Some(false),
			email=Some("user@test.com"),
			fullName=Some("Test User"),
			metadata=Map("age" -> 99),
			password=Some(PlaintextPassword("password123")),
			roles=Seq("test-role")
		)
		val expectedJson = """
		{
			"enabled": false,
			"email": "user@test.com",
			"full_name": "Test User",
			"metadata": {"age":99},
			"password": "password123",
			"roles": ["test-role"]
		}
		""".replace("\n", "").replace("\t", "")
		val formattedJson = spaceBetweenPunctuation.replaceAllIn(expectedJson, "$1$2")
		val result = CreateOrUpdateUserContentBuilder(create).string 
		result shouldBe formattedJson
	}

	test("CreateOrUpdateUserContentBuilder should handle update requests") {
		val create = CreateOrUpdateUserRequest(
			"test-user",
			UpdateUser,
			enabled=Some(false),
			metadata=Map("age" -> 100),
			password=None,
			roles=Seq("test-role")
		)
		val expectedJson = """
		{
			"enabled": false,
			"metadata": {"age":100},
			"roles": ["test-role"]
		}
		""".replace("\n", "").replace("\t", "")
		val formattedJson = spaceBetweenPunctuation.replaceAllIn(expectedJson, "$1$2")
		val result = CreateOrUpdateUserContentBuilder(create).string 
		result shouldBe formattedJson
	}

	test("CreateOrUpdateUserContentBuilder should throw an error if a CreateUser request does no provide a password") {
		val create = CreateOrUpdateUserRequest(
			"test-user",
			CreateUser,
			password=None,
			roles=Seq("test-role")
		)
		a [IllegalArgumentException] should be thrownBy CreateOrUpdateUserContentBuilder(create).string
	}
}
