package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.http.search.queries.span.XContentBuilderExtensions._
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import org.scalatest.FunSuite

import scala.util.parsing.json.JSON

class XContentBuilderExtensionsTest extends FunSuite {

  test("RichXContentBuilder.rawArrayValue should write not write bytes for empty array") {
    val builder = XContentFactory.jsonBuilder()
    builder.startArray()
    builder.rawArrayValue(Seq())
    builder.endArray()

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw("""[]""")
    assert(actual === expected)
  }

  test("RichXContentBuilder.rawArrayValue should write bytes array with one element") {
    val builder = XContentFactory.jsonBuilder()
    builder.startArray()
    builder.rawArrayValue(Seq(getSimpleObject("f1", "v1")))
    builder.endArray()

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw("""[{"f1": "v1"}]""")
    assert(actual === expected)
  }

  test("RichXContentBuilder.rawArrayValue should write bytes array many elements") {
    val builder = XContentFactory.jsonBuilder()
    builder.startArray()
    builder.rawArrayValue(Seq(
      getSimpleObject("f1", "v1"),
      getSimpleObject("f2", "v2"),
      getSimpleObject("f3", "v3")
    ))
    builder.endArray()

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw(
      """[
        |   {"f1": "v1"},
        |   {"f2": "v2"},
        |   {"f3": "v3"}
        |]""".stripMargin)
    assert(actual === expected)
  }

  private def getSimpleObject(field: String, value: String): XContentBuilder = {
    XContentFactory.jsonBuilder().startObject().field(field, value).endObject()
  }
}
