package com.sksamuel.elastic4s

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ElasticUrlEncoderTest extends AnyFunSuite with Matchers {

  test("encode plus signs as %2B") {
    val expected = "test%2B"
    val actual = ElasticUrlEncoder.encodeUrlFragment("test+")
    actual shouldEqual expected
  }

  test("encode spaces as %20") {
    val expected = "test%20test"
    val actual = ElasticUrlEncoder.encodeUrlFragment("test test")
    actual shouldEqual expected
  }
}
