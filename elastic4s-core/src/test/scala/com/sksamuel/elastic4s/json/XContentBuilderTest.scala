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

  test("should support double arrays") {
    XContentFactory.obj().field("doubles", Array(124.45, 962.23)) shouldBe """"""
  }

  test("should support string arrays") {
    XContentFactory.obj().field("doubles", Array("foo", "boo")) shouldBe """"""
  }

  test("should support double fields") {
    XContentFactory.obj().field("double", 5612.3734) shouldBe """"""
  }

  test("should support int fields") {
    XContentFactory.obj().field("double", 3242365) shouldBe """"""
  }

  test("should support long fields") {
    XContentFactory.obj().field("double", 91118592743568234L) shouldBe """"""
  }

  test("should support boolean fields") {
    XContentFactory.obj().field("double", true) shouldBe """"""
  }

  test("should support str fields") {
    XContentFactory.obj().field("double", "ewrewr") shouldBe """"""
  }
}
