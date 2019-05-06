package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.HttpEntity.StringEntity
import org.scalatest.{FlatSpec, Matchers}

class ElasticErrorTest extends FlatSpec with Matchers with ElasticDsl {

  "ElasticError" should "properly handle an error response with an invalid body" in {
    val error = ElasticError.parse(HttpResponse(123, Some(StringEntity("{", None)), Map()))
    assert(error.reason == "123")
  }

  it should "properly handle an error response with a missing body" in {
    val error = ElasticError.parse(HttpResponse(123, Some(StringEntity("", None)), Map()))
    assert(error.reason == "123")
  }

}
