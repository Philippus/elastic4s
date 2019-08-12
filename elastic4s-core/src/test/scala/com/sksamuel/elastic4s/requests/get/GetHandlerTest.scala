package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.HttpEntity.StringEntity
import com.sksamuel.elastic4s.{ElasticError, HttpResponse}
import org.scalatest.Matchers._
import org.scalatest.{EitherValues, FlatSpec}

class GetHandlersTest extends FlatSpec with GetHandlers with EitherValues{
  it should "handle proxy errors correctly" in {

    val responseBody = raw"""{"ok":false,"message":"Deployment is under maintenance / recovery."}"""
    val response = HttpResponse(503, Some(StringEntity(responseBody, None)), Map.empty)

    // hmm not sure we really want the body to be in type AND reason
    GetHandler.responseHandler.handle(response).left.value shouldBe ElasticError(responseBody, responseBody, None, None, None, Seq.empty, None)
  }

}
