package com.sksamuel.elastic4s.jackson

import org.scalatest.{FunSuite, Matchers}

class JacksonSupportTest extends FunSuite with Matchers {

  case class Bibble(double: Double)

  test("jackson should marshall doubles as numbers not scientifics") {
    JacksonSupport.mapper.writeValueAsString(Bibble(1.23)) shouldBe """{"double":1.23}"""
  }
}
