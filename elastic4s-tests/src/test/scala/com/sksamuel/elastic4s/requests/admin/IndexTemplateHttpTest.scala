package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.fields.TextField
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.indexes.CreateIndexTemplateRequest
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateRequest
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class IndexTemplateHttpTest
    extends AnyWordSpec
    with Matchers
    with Eventually
    with DockerTests {

  Try {
    client.execute {
      deleteIndex("brewers")
    }.await
    Thread.sleep(2000)
  }

  Try {
    client.execute {
      deleteIndexTemplate("brewery_template")
    }.await
    Thread.sleep(2000)
  }

  Try {
    client.execute {
      deleteIndexTemplate("dyntemplate_template")
    }.await
    Thread.sleep(2000)
  }

  "create template" should {
    "create template" in {

      val result = client.execute {
        CreateIndexTemplateRequest("brewery_template", Seq("brew*")).mappings(
          properties(
            textField("name"),
            doubleField("year_founded").ignoreMalformed(true)
          )
        )
      }.await.result
      result.acknowledged shouldBe true

      eventually {
        val resp = client.execute {
          getIndexTemplate("brewery_template")
        }.await
        resp.result.indexTemplates.find(_.name == "brewery_template").get.template.indexPatterns shouldBe Seq("brew*")
        resp.result.indexTemplates.find(_.name == "brewery_template").get.template.order shouldBe 0
      }
    }
    "deserialize dynamic_templates from get template response" in {

      val dynTemplate = DynamicTemplateRequest("es_fields", TextField("").analyzer("spanish"))
        .matchMappingType("string")
        .matching("*_es")

      client.execute {
        CreateIndexTemplateRequest("dyntemplate_template", Seq("dyntemplate*")).mappings(
          properties(keywordField("id")).dynamicTemplates(dynTemplate)
        )
      }.await.result.acknowledged shouldBe true

      eventually {
        val resp = client.execute {
          getIndexTemplate("dyntemplate_template")
        }.await

        val mappings = resp.result.indexTemplates.find(_.name == "dyntemplate_template").get.template.mappings
        mappings.dynamicTemplates should have size 1

        val tmpl = mappings.dynamicTemplates.head
        tmpl.name shouldBe "es_fields"
        tmpl.`match` shouldBe Some("*_es")
        tmpl.matchMappingType shouldBe Some("string")
        tmpl.mapping shouldBe a[TextField]
      }
    }
    "apply template to new indexes that match the pattern" in {

      // this should match the earlier template of brew*
      client.execute {
        createIndex("brewers")
      }.await

      client.execute {
        indexInto("brewers") fields (
          "name"         -> "fullers",
          "year_founded" -> 1829
        ) refresh RefreshPolicy.Immediate
      }.await

      // check that the document was indexed
      client.execute {
        search("brewers") query termQuery("year_founded", 1829)
      }.await.result.totalHits shouldBe 1

      // the mapping for this index should match the template
      val properties = client.execute {
        getMapping("brewers")
      }.await

      properties.result.head.index shouldBe "brewers"
      properties.result.head.mappings("year_founded") shouldBe Map("type" -> "double", "ignore_malformed" -> true)
    }
  }
}
