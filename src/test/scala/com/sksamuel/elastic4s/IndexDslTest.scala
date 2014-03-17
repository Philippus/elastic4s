package com.sksamuel.elastic4s

import org.scalatest.{ Matchers, FlatSpec, OneInstancePerTest }
import org.scalatest.mock.MockitoSugar
import ElasticDsl._

/** @author Stephen Samuel */
class IndexDslTest extends FlatSpec with MockitoSugar with JsonSugar with Matchers with OneInstancePerTest {
  "an index dsl" should "generate with index & type as a delimited string" in {
    val req = index into "twitter/tweets" fields Map(
      "name" -> "sksamuel"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json")
  }

  it should "generate with index & type as a tuple" in {
    val req = index into "twitter" -> "tweets" fields Map(
      "name" -> "sksamuel"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json")
  }

  it should "generate with a string id" in {
    val req = index into "twitter/tweets" id "test-id" fields Map(
      "name" -> "sksamuel"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json", Some("test-id"))
  }

  it should "generate with a numeric id" in {
    val req = index into "twitter/tweets" id 1234 fields Map(
      "name" -> "sksamuel"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json", Some(1234))
  }

  it should "generate including routing and ttl" in {
    val req = index into "twitter/tweets" fields Map(
      "name" -> "sksamuel"
    ) routing "users" ttl 100000

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json", None, Some("users"), Some(100000))
  }

  it should "generate for multiple fields" in {
    val req = index into "twitter/tweets" fields Map(
      "user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_multiple.json")
  }

  it should "generate for multiple fields when using a seq" in {
    val req = index into "twitter/tweets" fields Seq(
      "user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_multiple.json")
  }

  it should "generate nested fields" in {
    val req = index into "twitter/tweets" fields Map(
      "user" -> Map(
        "handle" -> "sammy",
        "name" -> "Sam"
      ),
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "Nested message"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/nested.json")
  }

  it should "generate array fields" in {
    val req = index into "twitter/tweets" fields Map(
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "Array message",
      "tags" -> Array(
        "array",
        "search",
        "test"
      )
    )

    checkRequest(req, "twitter", "tweets", "/json/index/array.json")
  }

  it should "generate array of nested fields" in {
    val req = index into "twitter/tweets" fields Map(
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "Array of nested message",
      "tags" -> Array(
        Map(
          "id" -> 642,
          "text" -> "array"
        ),
        Map(
          "id" -> 883,
          "text" -> "search"
        ),
        Map(
          "id" -> 231,
          "text" -> "test"
        )
      )
    )

    checkRequest(req, "twitter", "tweets", "/json/index/array_nested.json")
  }

  it should "generate nested field of arrays" in {
    val req = index into "twitter/tweets" fields Map(
      "user" -> Map(
        "handle" -> "sammy",
        "name" -> "Sam",
        "hobbies" -> Array(
          "search",
          "scala"
        )
      ),
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "Nested array message"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/nested_array.json")
  }

  it should "generate null fields" in {
    val req = index into "twitter/tweets" fields Map(
      "user" -> "sammy",
      "message" -> null
    )

    checkRequest(req, "twitter", "tweets", "/json/index/null.json")
  }

  it should "generate nested null fields" in {
    val req = index into "twitter/tweets" fields Map(
      "user" -> Map(
        "handle" -> "sammy",
        "name" -> null
      ),
      "message" -> "Message with nested null"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/nested_null.json")
  }

  private def checkRequest(req: IndexDefinition,
                           expectedIndex: String,
                           expectedType: String,
                           expectedJsonResource: String,
                           expectedId: Option[Any] = None,
                           expectedRouting: Option[String] = None,
                           expectedTtl: Option[Long] = None) {
    val builtRequest = req.build

    builtRequest.index shouldEqual expectedIndex
    builtRequest.`type` shouldEqual expectedType

    expectedId match {
      case Some(i) => builtRequest.id shouldEqual i.toString
      case None => builtRequest.id shouldBe null
    }

    expectedRouting match {
      case Some(r) => builtRequest.routing shouldEqual r
      case None => builtRequest.routing shouldBe null
    }

    expectedTtl match {
      case Some(t) => builtRequest.ttl shouldEqual t
      case None => builtRequest.ttl shouldEqual -1
    }

    req._fieldsAsXContent.string should matchJsonResource(expectedJsonResource)
  }
}
