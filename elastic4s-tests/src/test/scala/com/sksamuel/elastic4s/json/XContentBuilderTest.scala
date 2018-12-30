package com.sksamuel.elastic4s.json

import com.sksamuel.elastic4s.XContentFactory
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
    """{"wibble":{"foo":1,"boo":true},"dibble":{"goo":2.4}}"""
  }

  test("should support raw fields in objects") {
    XContentFactory.obj().rawField("nested", """{"test":true,"name":"foo"}""").string shouldBe """{"nested":{"test":true,"name":"foo"}}"""
  }

  test("should support raw values in arrays") {
    XContentFactory.array().rawValue("""{"test":true,"name":"foo"}""").string shouldBe """[{"test":true,"name":"foo"}]"""
  }

  test("should support boolean arrays") {
    XContentFactory.obj().array("booleans", Array(true, false, true)).string shouldBe """{"booleans":[true,false,true]}"""
  }

  test("should support double arrays") {
    XContentFactory.obj().array("doubles", Array(124.45, 962.23)).string shouldBe """{"doubles":[124.45,962.23]}"""
  }

  test("should support long arrays") {
    XContentFactory.obj().array("longs", Array(345345435345L, 3257059014L)).string shouldBe """{"longs":[345345435345,3257059014]}"""
  }

  test("should support string arrays") {
    XContentFactory.obj().array("strings", Array("foo", "boo")).string shouldBe """{"strings":["foo","boo"]}"""
  }

  test("should support double fields") {
    XContentFactory.obj().field("double", 5612.3734).string shouldBe """{"double":5612.3734}"""
  }

  test("should support int fields") {
    XContentFactory.obj().field("int", 3242365).string shouldBe """{"int":3242365}"""
  }

  test("should support long fields") {
    XContentFactory.obj().field("long", 91118592743568234L).string shouldBe """{"long":91118592743568234}"""
  }

  test("should support boolean fields") {
    XContentFactory.obj().field("boolean", true).string shouldBe """{"boolean":true}"""
  }

  test("should support str fields") {
    XContentFactory.obj().field("str", "ewrewr").string shouldBe
      """{"str":"ewrewr"}"""
  }
}
