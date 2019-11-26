package com.sksamuel.elastic4s.requests.ingest

import com.sksamuel.elastic4s.{ElasticRequest, HttpEntity, XContentFactory}
import org.scalatest.{FlatSpec, Matchers}

class PutPipelineRequestHandlerTest extends FlatSpec with IngestHandlers with Matchers {

  import PutPipelineRequestHandler._

  // See the docs: https://www.elastic.co/guide/en/elasticsearch/reference/master/geoip-processor.html#geoip-processor
  it should "build a pipeline request with a geoip processor" in {
    val req = PutPipelineRequest("geoip", "Add geoip info", Processor("geoip", "{\"field\": \"ip\"}"))
    val correctJson =
      XContentFactory.parse("""
        |{
        |  "description" : "Add geoip info",
        |  "processors" : [
        |    {
        |      "geoip" : {
        |        "field" : "ip"
        |      }
        |    }
        |  ]
        |}
        |""".stripMargin).string()
    build(req) shouldBe ElasticRequest("PUT", "_ingest/pipeline/geoip", HttpEntity(correctJson))
  }

  it should "build a pipeline request with a langdetect processor" in {
    val req = PutPipelineRequest("langdetect-pipeline", "A pipeline to do whatever",
      Processor("langdetect", "{\"field\": \"my_field\", \"target_field\": \"language\" }"))
    val correctJson = XContentFactory.parse(
      """
        |{
        |  "description": "A pipeline to do whatever",
        |  "processors": [
        |    {
        |      "langdetect" : {
        |        "field" : "my_field",
        |        "target_field" : "language"
        |      }
        |    }
        |  ]
        |}
        |
        |""".stripMargin).string()
    build(req) shouldBe ElasticRequest("PUT", "_ingest/pipeline/langdetect-pipeline", HttpEntity(correctJson))
  }

  it should "build a pipeline request with multiple processors" in {
    val req = PutPipelineRequest("multi-pipeline", "A pipeline with multiple processors",
      Seq(
        Processor("geoip", "{\"field\": \"ip\"}"),
        Processor("langdetect", "{\"field\": \"my_field\", \"target_field\": \"language\" }")
      ))
    val correctJson = XContentFactory.parse(
      """
        |{
        |  "description": "A pipeline with multiple processors",
        |  "processors": [
        |    {
        |      "geoip" : {
        |        "field" : "ip"
        |      }
        |    },
        |    {
        |      "langdetect" : {
        |        "field" : "my_field",
        |        "target_field" : "language"
        |      }
        |    }
        |  ]
        |}
        |""".stripMargin).string()
    build(req) shouldBe ElasticRequest("PUT", "_ingest/pipeline/multi-pipeline", HttpEntity(correctJson))
  }


}

