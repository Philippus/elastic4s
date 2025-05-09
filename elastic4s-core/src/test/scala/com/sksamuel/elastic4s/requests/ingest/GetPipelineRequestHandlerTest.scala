package com.sksamuel.elastic4s.requests.ingest

import com.sksamuel.elastic4s.HttpEntity.StringEntity
import com.sksamuel.elastic4s.{ElasticRequest, HttpResponse}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GetPipelineRequestHandlerTest extends AnyFlatSpec with IngestHandlers with Matchers {

  import GetPipelineRequestHandler._

  it should "build a get pipeline request" in {
    val req = GetPipelineRequest("test-pipeline")

    build(req) shouldBe ElasticRequest("GET", "/_ingest/pipeline/test-pipeline")
  }

  it should "parse a get pipeline response" in {
    val responseBody =
      """
        |{
        |  "test-pipeline" : {
        |    "description" : "describe pipeline",
        |    "version" : 123,
        |    "processors" : [
        |      {
        |        "set" : {
        |          "field" : "foo",
        |          "value" : "bar"
        |        }
        |      },
        |      {
        |        "geoip" : {
        |          "field" : "ip",
        |          "target_field" : "geo",
        |          "database_file" : "GeoLite2-Country.mmdb",
        |          "properties": ["continent_name","region_name","city_name"],
        |          "ignore_missing": true,
        |          "first_only": false
        |        }
        |      }
        |    ]
        |  }
        |}
        |""".stripMargin
    val response     = HttpResponse(200, Some(StringEntity(responseBody, None)), Map.empty)

    responseHandler.handle(response).toOption.get shouldBe
      GetPipelineResponse(
        "test-pipeline",
        "describe pipeline",
        Some(123),
        Seq(
          CustomProcessor("set", """{"field":"foo","value":"bar"}"""),
          GeoIPProcessor(
            "ip",
            Some("geo"),
            Some("GeoLite2-Country.mmdb"),
            Some(Seq("continent_name", "region_name", "city_name")),
            Some(true),
            Some(false)
          )
        )
      )
  }

  it should "parse a get pipeline response with minimal values" in {
    val responseBody =
      """
        |{
        |  "test-pipeline" : {
        |    "description" : "describe pipeline",
        |    "processors" : []
        |  }
        |}
        |""".stripMargin
    val response     = HttpResponse(200, Some(StringEntity(responseBody, None)), Map.empty)

    responseHandler.handle(response).toOption.get shouldBe
      GetPipelineResponse(
        "test-pipeline",
        "describe pipeline",
        None,
        Seq.empty
      )
  }
}
