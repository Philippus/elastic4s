package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.HttpEntity.StringEntity
import com.sksamuel.elastic4s.requests.searches.SearchHandlers.MultiSearchHandler
import com.sksamuel.elastic4s.{ElasticError, HttpResponse}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MultiSearchHandlerTest extends AnyFlatSpec with Matchers with EitherValues {

  it should "handle error responses properly" in {
    val responseBody = """{"error":{"type":"some_error_type","reason":"some_error_reason","root_cause":[]},"status":400}"""
    val response = HttpResponse(400, Some(StringEntity(responseBody, None)), Map.empty)

    MultiSearchHandler.responseHandler.handle(response).left.value shouldBe ElasticError("some_error_type", "some_error_reason", None, None, None, Seq.empty, None)
  }

  it should "handle successful responses properly" in {
    val responseBody =
      """{
        |    "took": 1,
        |    "responses": [
        |        {
        |            "took": 1,
        |            "timed_out": false,
        |            "_shards": {
        |                "total": 1,
        |                "successful": 1,
        |                "skipped": 0,
        |                "failed": 0
        |            },
        |            "hits": {
        |                "total": {
        |                    "value": 0,
        |                    "relation": "eq"
        |                },
        |                "max_score": null,
        |                "hits": []
        |            },
        |            "status": 200
        |        },
        |        {
        |            "error": {
        |                "type": "some_error_type",
        |                "reason": "some_error_reason",
        |                "root_cause": []
        |            },
        |            "status": 400
        |        }
        |    ]
        |}""".stripMargin
    val response = HttpResponse(200, Some(StringEntity(responseBody, None)), Map.empty)
    val mResponse = MultiSearchHandler.responseHandler.handle(response).right.value
    mResponse.items should have size 2
    mResponse.items.map(_.status) shouldEqual Seq(200, 400)
  }
}
