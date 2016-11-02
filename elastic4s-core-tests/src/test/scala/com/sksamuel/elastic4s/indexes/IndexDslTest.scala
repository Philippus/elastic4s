package com.sksamuel.elastic4s.indexes

import java.text.SimpleDateFormat
import java.util.{Calendar, Date, GregorianCalendar}

import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.mappings.{FieldValue, SimpleFieldValue}
import org.elasticsearch.common.xcontent.XContentBuilder
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers, OneInstancePerTest}

class IndexDslTest extends FlatSpec with MockitoSugar with JsonSugar with Matchers with OneInstancePerTest {

  import com.sksamuel.elastic4s.ElasticDsl._

  "an index dsl" should "generate with index & type as a delimited string" in {
    val req = index into "twitter/tweets" fields Map(
      "name" -> "sksamuel"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json")
  }

  it should "generate with index & type as a tuple" in {
    val req = index into "twitter" -> "tweets" fields (
      "name" -> "sksamuel"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json")
  }

  it should "generate with a string id" in {
    val req = index into "twitter/tweets" id "test-id" fields (
      "name" -> "sksamuel"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json", Some("test-id"))
  }

  it should "generate with a numeric id" in {
    val req = index into "twitter/tweets" id 1234 fields (
      "name" -> "sksamuel"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json", Some(1234))
  }

  it should "generate including routing and ttl" in {
    val req = index into "twitter/tweets" fields (
      "name" -> "sksamuel"
    ) routing "users" ttl 100000

    checkRequest(req, "twitter", "tweets", "/json/index/simple_single.json", None, Some("users"), Some(100000))
  }

  it should "generate for multiple fields" in {
    val req = index into "twitter/tweets" fields (
      "user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_multiple.json")
  }

  it should "generate for multiple fields when using a map" in {
    val req = index into "twitter/tweets" fields Map(
      "user" -> "sammy",
      "post_date" -> "2009-11-15T14:12:12",
      "message" -> "trying out Elastic Search Scala DSL"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_multiple.json")
  }

  it should "generate nested fields" in {
    val req = index into "twitter/tweets" fields (
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
    val req = index into "twitter/tweets" fields (
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

  it should "generate array fields when using seqs" in {
    val req = index into "twitter/tweets" fields (
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "Array message",
      "tags" -> Seq(
        "array",
        "search",
        "test"
      )
    )

    checkRequest(req, "twitter", "tweets", "/json/index/array.json")
  }

  it should "generate array of nested fields" in {
    val req = index into "twitter/tweets" fields (
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

  it should "generate array of nested fields when using seqs" in {
    val req = index into "twitter/tweets" fields (
      "user" -> "sammy",
      "post_date" -> "2011-11-15T14:12:12",
      "message" -> "Array of nested message",
      "tags" -> Seq(
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
    val req = index into "twitter/tweets" fields (
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
    val req = index into "twitter/tweets" fields (
      "user" -> "sammy",
      "message" -> null
    )

    checkRequest(req, "twitter", "tweets", "/json/index/null.json")
  }

  it should "generate nested null fields" in {
    val req = index into "twitter/tweets" fields (
      "user" -> Map(
        "handle" -> "sammy",
        "name" -> null
      ),
        "message" -> "Message with nested null"
    )

    checkRequest(req, "twitter", "tweets", "/json/index/nested_null.json")
  }

  it should "field values" in {
    val req = index into "twitter/tweets" fieldValues (
      SimpleFieldValue("user", "sammy"),
      SimpleFieldValue("post_date", "2009-11-15T14:12:12"),
      SimpleFieldValue("message", "trying out Elastic Search Scala DSL")
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_multiple.json")
  }

  it should "custom field values" in {
    val cal = new GregorianCalendar()
    cal.set(Calendar.YEAR, 2009)
    cal.set(Calendar.MONTH, 10)
    cal.set(Calendar.DAY_OF_MONTH, 15)
    cal.set(Calendar.HOUR_OF_DAY, 14)
    cal.set(Calendar.MINUTE, 12)
    cal.set(Calendar.SECOND, 12)

    val req = index into "twitter/tweets" fieldValues (
      SimpleFieldValue("user", "sammy"),
      CustomDateFieldValue("post_date", cal.getTime),
      SimpleFieldValue("message", "trying out Elastic Search Scala DSL")
    )

    checkRequest(req, "twitter", "tweets", "/json/index/simple_multiple.json")
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
      case Some(t) => builtRequest.ttl.millis() shouldEqual t
      case None => builtRequest.ttl shouldEqual null
    }

    req._fieldsAsXContent.string should matchJsonResource(expectedJsonResource)
  }

  case class CustomDateFieldValue(name: String, date: Date) extends FieldValue {
    private val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    def output(source: XContentBuilder): Unit = {
      source.field(name, dateFormat.format(date))
    }
  }
}
