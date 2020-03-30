package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.json.SourceAsContentBuilder
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SourceAsContentBuilderTest extends AnyFunSuite with Matchers {

  test("source as content builder should handle tuples") {
    val map = Map("name" -> "sammy", "teams" -> Seq(("football", "boro"), ("baseball", "phillies")), "projects" -> null)
    SourceAsContentBuilder(map).string() shouldBe """{"name":"sammy","teams":[["football","boro"],["baseball","phillies"]],"projects":null}"""
  }

  test("source as content builder should handle bigdecimals") {
    val map = Map("dec" -> BigDecimal("9223372036854776000"))
    SourceAsContentBuilder(map).string() shouldBe """{"dec":9.223372036854776E+18}"""
  }
}
