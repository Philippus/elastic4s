package com.sksamuel.elastic4s.json

import java.math.BigInteger

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class XContentBuilderTest extends AnyFunSuite with Matchers {

  test("simple object") {
    XContentFactory.obj().field("test", true).field("name", "foo").string shouldBe
      """{"test":true,"name":"foo"}"""
  }

  test("simple array") {
    XContentFactory.array().value("foo").value("boo").string shouldBe
      """["foo","boo"]"""
  }

  test("nested objects") {
    XContentFactory.obj()
      .startObject("wibble").field("foo", 1).field("boo", true).endObject()
      .startObject("dibble").field("goo", 2.4).string shouldBe
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

  test("should support biginteger arrays") {
    XContentFactory.obj().autoarray("bigintegers", Seq(new BigInteger("123"), new BigInteger("456"))).string shouldBe """{"bigintegers":[123,456]}"""
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

  test("should support bigdecimal fields") {
    XContentFactory.obj().field("dec", BigDecimal("291839123.12321312")).string shouldBe """{"dec":291839123.12321312}"""
  }

  test("should support bigint fields") {
    XContentFactory.obj().field("bigint", BigInt("98123981231982361893619")).string shouldBe """{"bigint":98123981231982361893619}"""
  }

  test("should support biginteger fields") {
    XContentFactory.obj().autofield("biginteger", new BigInteger("98123981231982361893619")).string shouldBe """{"biginteger":98123981231982361893619}"""
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
