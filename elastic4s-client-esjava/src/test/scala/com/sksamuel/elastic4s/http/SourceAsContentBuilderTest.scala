package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.SourceAsContentBuilder
import org.scalatest.{FunSuite, Matchers}

class SourceAsContentBuilderTest extends FunSuite with Matchers {

  test("source as content builder should handle tuples") {
    val map = Map("name" -> "sammy", "teams" -> Seq(("football", "boro"), ("baseball", "phillies")), "projects" -> null)
    SourceAsContentBuilder(map).string() shouldBe """{"name":"sammy","teams":[["football","boro"],["baseball","phillies"]],"projects":null}"""
  }
}
