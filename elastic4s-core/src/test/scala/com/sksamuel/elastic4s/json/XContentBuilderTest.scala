package com.sksamuel.elastic4s.json

import org.scalatest.{FunSuite, Matchers}

class XContentBuilderTest extends FunSuite with Matchers {

  test("simple object") {
    XContentFactory.obj().field("test", true).field("name", "foo").string() shouldBe
      """{"test":true,"name":"foo"}"""
  }

  test("simple array") {
    XContentFactory.array().value("foo").value("boo").string() shouldBe
      """["foo","boo"]"""
  }

  test("nested objects") {
    XContentFactory.obj()
      .startObject("wibble").field("foo", 1).field("boo", true).endObject()
      .startObject("dibble").field("goo", 2.4).string() shouldBe
    """{"wibble":{"foo":1.0,"boo":true},"dibble":{"goo":2.4}}"""
  }
}
