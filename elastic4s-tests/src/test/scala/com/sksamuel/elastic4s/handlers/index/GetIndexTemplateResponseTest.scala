package com.sksamuel.elastic4s.handlers.index

import com.sksamuel.elastic4s.HttpEntity.StringEntity
import com.sksamuel.elastic4s.HttpResponse
import com.sksamuel.elastic4s.fields.{KeywordField, TextField}
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateRequest
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class GetIndexTemplateResponseTest extends AnyFlatSpec with IndexTemplateHandlers with Matchers with EitherValues {

  private def fixture(path: String): String =
    Source.fromInputStream(getClass.getResourceAsStream(path)).mkString

  "GetIndexTemplateHandler" should "deserialize dynamic_templates from the array-of-single-key-objects format" in {
    val json     = fixture("/json/index/get_index_template_response.json")
    val response = HttpResponse(200, Some(StringEntity(json, None)), Map.empty)

    val result = GetIndexTemplateHandler.responseHandler.handle(response).value

    result.indexTemplates should have size 1
    val tmpl = result.indexTemplates.head
    tmpl.name shouldBe "my_template"

    val mappings = tmpl.template.mappings
    mappings.dynamicTemplates should have size 2
  }

  it should "correctly parse the first dynamic template (es)" in {
    val json     = fixture("/json/index/get_index_template_response.json")
    val response = HttpResponse(200, Some(StringEntity(json, None)), Map.empty)

    val mappings = GetIndexTemplateHandler.responseHandler.handle(response).value
      .indexTemplates.head.template.mappings

    val es = mappings.dynamicTemplates.find(_.name == "es").get
    es.`match` shouldBe Some("*_es")
    es.MatchPattern shouldBe Some("regex")
    es.matchMappingType shouldBe Some("string")
    es.mapping shouldBe a[TextField]
    es.mapping.asInstanceOf[TextField].analyzer shouldBe Some("spanish")
  }

  it should "correctly parse the second dynamic template (en)" in {
    val json     = fixture("/json/index/get_index_template_response.json")
    val response = HttpResponse(200, Some(StringEntity(json, None)), Map.empty)

    val mappings = GetIndexTemplateHandler.responseHandler.handle(response).value
      .indexTemplates.head.template.mappings

    val en = mappings.dynamicTemplates.find(_.name == "en").get
    en.`match` shouldBe Some("*")
    en.matchMappingType shouldBe Some("string")
    en.MatchPattern shouldBe None
    en.mapping shouldBe a[TextField]
    en.mapping.asInstanceOf[TextField].analyzer shouldBe Some("english")
  }

  it should "deserialize properties from mappings" in {
    val json     = fixture("/json/index/get_index_template_response.json")
    val response = HttpResponse(200, Some(StringEntity(json, None)), Map.empty)

    val mappings = GetIndexTemplateHandler.responseHandler.handle(response).value
      .indexTemplates.head.template.mappings

    mappings.properties should have size 2
    val fieldNames = mappings.properties.map(_.name).toSet
    fieldNames shouldBe Set("title", "created_at")

    val title = mappings.properties.find(_.name == "title").get
    title shouldBe a[KeywordField]
  }

  it should "return empty TemplateMappings when mappings is absent" in {
    val json =
      """{
        |  "index_templates": [
        |    {
        |      "name": "empty_template",
        |      "index_template": {
        |        "index_patterns": ["empty_*"],
        |        "priority": 1,
        |        "version": 1,
        |        "composed_of": []
        |      }
        |    }
        |  ]
        |}""".stripMargin
    val response = HttpResponse(200, Some(StringEntity(json, None)), Map.empty)

    val mappings = GetIndexTemplateHandler.responseHandler.handle(response).value
      .indexTemplates.head.template.mappings

    mappings.dynamicTemplates shouldBe empty
    mappings.properties shouldBe empty
  }

  it should "return empty dynamic_templates when mappings has only properties" in {
    val json =
      """{
        |  "index_templates": [
        |    {
        |      "name": "props_only",
        |      "index_template": {
        |        "index_patterns": ["props_*"],
        |        "template": {
        |          "mappings": {
        |            "properties": {
        |              "name": { "type": "keyword" }
        |            }
        |          }
        |        },
        |        "priority": 1,
        |        "version": 1,
        |        "composed_of": []
        |      }
        |    }
        |  ]
        |}""".stripMargin
    val response = HttpResponse(200, Some(StringEntity(json, None)), Map.empty)

    val mappings = GetIndexTemplateHandler.responseHandler.handle(response).value
      .indexTemplates.head.template.mappings

    mappings.dynamicTemplates shouldBe empty
    mappings.properties should have size 1
  }
}
