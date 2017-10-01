package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.scalatest.{FunSuite, Matchers}

class ScriptBuilderFnTest extends FunSuite with Matchers {

  test("should handle recursive maps") {
    ScriptBuilderFn(ScriptDefinition("myscript", params = Map("a" -> 1.2, "b" -> Map("c" -> true, "d" -> List(Map("e" -> 3)))))).string shouldBe
      """{"inline":"myscript","params":{"a":1.2,"b":{"c":true,"d":[{"e":3}]}}}"""
  }
}
