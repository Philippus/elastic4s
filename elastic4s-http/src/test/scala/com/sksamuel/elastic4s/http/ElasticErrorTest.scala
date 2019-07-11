package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.http.HttpEntity.StringEntity
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}


class ElasticErrorTest extends FlatSpec with Matchers with ElasticDsl with MockitoSugar {

  "ElasticError" should "properly handle an error response with an incorrect body" in {
    val error = ElasticError.parse(HttpResponse(123, Some(StringEntity("{", None)), Map()))
    assert(error.reason == "123")
  }

}
