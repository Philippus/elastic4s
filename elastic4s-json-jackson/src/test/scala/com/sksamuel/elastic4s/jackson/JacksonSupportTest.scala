package com.sksamuel.elastic4s.jackson

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class JacksonSupportTest extends AnyFunSuite with Matchers {

  case class Bibble(double: Double)

  test("jackson should marshall doubles as numbers not scientifics") {
    JacksonSupport.mapper.writeValueAsString(Bibble(1.23)) shouldBe """{"double":1.23}"""
  }
}
