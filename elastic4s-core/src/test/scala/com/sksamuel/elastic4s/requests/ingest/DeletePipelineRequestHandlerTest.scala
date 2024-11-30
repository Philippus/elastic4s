package com.sksamuel.elastic4s.requests.ingest

import com.sksamuel.elastic4s.HttpEntity.StringEntity
import com.sksamuel.elastic4s.{ElasticRequest, HttpResponse}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DeletePipelineRequestHandlerTest extends AnyFlatSpec with IngestHandlers with Matchers {

  import DeletePipelineRequestHandler._

  it should "build a delete pipeline request" in {
    val req = DeletePipelineRequest("test-pipeline")

    build(req) shouldBe ElasticRequest("DELETE", "/_ingest/pipeline/test-pipeline")
  }

  it should "parse a delete pipeline response" in {
    val responseBody =
      """
        |{
        |  "acknowledged" : true
        |}
        |""".stripMargin
    val response     = HttpResponse(200, Some(StringEntity(responseBody, None)), Map.empty)

    responseHandler.handle(response).right.get shouldBe DeletePipelineResponse(true)
  }
}
