package com.sksamuel.elastic4s.requests.ingest

import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.{ElasticRequest, HttpEntity}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PutPipelineRequestHandlerTest extends AnyFlatSpec with IngestHandlers with Matchers {

  import PutPipelineRequestHandler._

  // Docs: https://www.elastic.co/guide/en/elasticsearch/reference/master/geoip-processor.html#geoip-processor
  it should "build a pipeline request with a geoip processor using the RawProcessor case class" in {
    val req = PutPipelineRequest("geoip", "Add geoip info", CustomProcessor("geoip", "{\"field\": \"ip\"}"))
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

  it should "build a pipeline request with a geoip processor using the strongly-typed case class" in {
    val proc = GeoIPProcessor("ip", Some("geo"), Some("GeoLite2-Country.mmdb"))
    val req = PutPipelineRequest("geoip", "Add geoip info", proc)
    val correctJson = XContentFactory.parse(
      """
        |{
        |  "description" : "Add geoip info",
        |  "processors" : [
        |    {
        |      "geoip" : {
        |        "field" : "ip",
        |        "target_field" : "geo",
        |        "database_file" : "GeoLite2-Country.mmdb"
        |      }
        |    }
        |  ]
        |}
        |""".stripMargin).string()

    build(req) shouldBe ElasticRequest("PUT", "_ingest/pipeline/geoip", HttpEntity(correctJson))
  }

  // Docs: https://github.com/spinscale/elasticsearch-ingest-langdetect#usage
  it should "build a pipeline request with a langdetect processor" in {
    val req = PutPipelineRequest("langdetect-pipeline", "A pipeline to do whatever",
      CustomProcessor("langdetect", "{\"field\": \"my_field\", \"target_field\": \"language\" }"))
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
        GeoIPProcessor(field = "ip"),
        CustomProcessor("langdetect", "{\"field\": \"my_field\", \"target_field\": \"language\" }")
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
