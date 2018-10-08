package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.http.search.queries.span.XContentBuilderExtensions._
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import org.scalatest.FunSuite
import org.scalatest.Matchers._

import scala.util.parsing.json.JSON

class XContentBuilderExtensionsTest extends FunSuite {

  test("RichXContentBuilder.addArray should not write bytes for empty array") {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.addArray("arrayField", Seq())
    builder.endObject()

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw("""{"arrayField": []}""")
    assert(actual === expected)
  }

  test("RichXContentBuilder.addArray should write bytes array with one element") {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.addArray("arrayField", Seq(buildSimpleObject("f1", "v1")))
    builder.endObject()

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw("""{"arrayField": [{"f1": "v1"}]}""")
    assert(actual === expected)
  }

  test("RichXContentBuilder.addArray should write bytes array with many elements") {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.addArray("arrayField", Seq(
      buildSimpleObject("f1", "v1"),
      buildSimpleObject("f2", "v2"),
      buildSimpleObject("f3", "v3")
    ))
    builder.endObject()

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw(
      """{
        |  "arrayField": [
        |    {"f1": "v1"},
        |    {"f2": "v2"},
        |    {"f3": "v3"}
        |  ]
        |}""".stripMargin)
    assert(actual === expected)
  }

  test("RichXContentBuilder.addArray should not write extra zero bytes between/after comma character, because ES fails to handle them") {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.addArray("arrayField", Seq(
      buildSimpleObject("f1", "v1"),
      buildSimpleObject("f2", "v2"),
      buildSimpleObject("f3", "v3")
    ))
    builder.endObject()

    val result = builder.string()
    result should not contain '\u0000'
    result shouldBe """{"arrayField":[{"f1":"v1"},{"f2":"v2"},{"f3":"v3"}]}"""
  }

  private def buildSimpleObject(field: String, value: String): XContentBuilder = {
    XContentFactory.jsonBuilder().startObject().field(field, value).endObject()
  }
}
