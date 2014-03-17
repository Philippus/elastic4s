package com.sksamuel.elastic4s

import org.scalatest.{ Matchers, FlatSpec, OneInstancePerTest }
import org.scalatest.mock.MockitoSugar
import ElasticDsl._

/** @author Stephen Samuel */
class IndexDslTest extends FlatSpec with MockitoSugar with JsonSugar with Matchers with OneInstancePerTest {
  "an index dsl" should "accept index and type as a / delimited string" in {
    val req = index into "twitter/tweets" id "thisid" fields Map(
      "name" -> "sksamuel"
    )

    req._fieldsAsXContent.string should matchJsonResource("/json/index/index_test1.json")
  }

  it should "accept index and type as a tuple" in {
    val req = index into "twitter" -> "tweets" fields Map(
      "name" -> "sksamuel"
    )

    req._fieldsAsXContent.string should matchJsonResource("/json/index/index_test2.json")
  }

  it should "generate json for all fields" in {
    val req = index into "twitter/tweet" id 1234 fields Map(
      "user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL"
    )

    req._fieldsAsXContent.string should matchJsonResource("/json/index/index_test3.json")
  }

  it should "generate json for fields when using a seq" in {
    val req = index into "twitter/tweet" id 1234 fields Seq(
      "user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL")
    req._fieldsAsXContent.string should matchJsonResource("/json/index/index_test4.json")
  }

  it should "not include id when id is not specified" in {
    val req = index into "twitter/tweet" fields Map(
      "user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL"
    )
    req._fieldsAsXContent.string should matchJsonResource("/json/index/index_test5.json")
  }

  it should "include id when id is specified" in {
    val req = index into "twitter/tweet" id 9999 fields Map(
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "I have an ID"
    ) routing "users" ttl 100000
    req._fieldsAsXContent.string should matchJsonResource("/json/index/index_test6.json")
  }

  it should "nest" in {
    val req = index into "twitter/tweet" id 9999 fields Map(
      "user" -> Map(
        "handle" -> "sammy",
        "name" -> "Sam"
      ),
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "Nested message"
    )
    req._fieldsAsXContent.string should matchJsonResource("/json/index/nested.json")
  }

  it should "array" in {
    val req = index into "twitter/tweet" id 9999 fields Map(
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "Array message",
      "tags" -> Array(
        "array",
        "search",
        "test"
      )
    )
    req._fieldsAsXContent.string should matchJsonResource("/json/index/array.json")
  }

  it should "array_nested" in {
    val req = index into "twitter/tweet" id 9999 fields Map(
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
    req._fieldsAsXContent.string should matchJsonResource("/json/index/array_nested.json")
  }

  it should "nested array" in {
    val req = index into "twitter/tweet" id 9999 fields Map(
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
    req._fieldsAsXContent.string should matchJsonResource("/json/index/nested_array.json")
  }
}
