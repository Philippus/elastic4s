package com.sksamuel.elastic4s.http

import org.scalatest.{FunSuite, Matchers}

class SourceAsContentBuilderTest extends FunSuite with Matchers {

  test("source as content builder should handle tuples") {
    val map = Map("name" -> "sammy", "teams" -> Seq(("football", "boro"), ("baseball", "phillies")), "projects" -> null)
    SourceAsContentBuilder(map).string() shouldBe """{"name":"sammy","teams":[["football","boro"],["baseball","phillies"]],"projects":null}"""
  }

  test("source as content builder should handle bigdecimals") {
    val map = Map("dec" -> BigDecimal("9223372036854776000"))
    SourceAsContentBuilder(map).string() shouldBe """{"dec":9.223372036854776E+18}"""
  }
}
