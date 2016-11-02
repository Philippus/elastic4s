package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.mappings.FieldType.DoubleType
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class DynamicTemplateDefinitionTest extends WordSpec with Matchers with ElasticSugar {

  "DynamicTemplateDefinition" should {
    "support mappings for wildcards" in {

      val priceTemplate = DynamicTemplateDefinition("price", field typed DoubleType).matching("*_price")

      val indexName = "dynamic_template_definition1"
      client.execute {
        create index indexName mappings {
          mapping(indexName) dynamicTemplates priceTemplate
        }
      }.await

      client.execute {
        index into indexName / "t" fields "my_price" -> 21.3
      }.await

      val resp = client.execute {
        getMapping(indexName / "t")
      }.await

      // should be detected and set to a double field
      resp.mappings(indexName)("t").source.toString shouldBe """{"t":{"properties":{"my_price":{"type":"double"}}}}"""
    }
    "support unmatch" in {

      val template = DynamicTemplateDefinition("price", field typed DoubleType).matching("*_price").unmatch("my_price")

      val indexName = "dynamic_template_definition2"
      client.execute {
        create index indexName mappings {
          mapping(indexName) dynamicTemplates template
        }
      }.await

      client.execute {
        index into indexName / "v" fields "my_price" -> "22"
      }.await

      val resp = client.execute {
        getMapping(indexName / "v")
      }.await

      // should have been ignored and left as String
      resp.mappings(indexName)("v").source.toString shouldBe """{"v":{"properties":{"my_price":{"type":"string"}}}}"""
    }
  }
}
