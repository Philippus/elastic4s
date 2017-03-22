package com.sksamuel.elastic4s.admin

import java.util

import com.sksamuel.elastic4s.analyzers.StandardAnalyzerDefinition
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class IndexTemplateTest extends WordSpec with MockitoSugar with ElasticSugar with Matchers {

  "create template" ignore {
    "be stored" in {

      client.execute {
        createTemplate("brewery_template").pattern("brew*").mappings(
          mapping("brands").fields(
            textField("name"),
            doubleField("year_founded") analyzer "test_analyzer"
          )
        ).analysis(StandardAnalyzerDefinition("test_analyzer"))
      }.await

      val resp = client.execute {
        getTemplate("brewery_template")
      }.await

      resp.getIndexTemplates.get(0).name shouldBe "brewery_template"
      resp.getIndexTemplates.get(0).template() shouldBe "brew*"

      val source = resp.getIndexTemplates.get(0).getMappings.valuesIt().next().toString
      source shouldBe """{"brands":{"properties":{"name":{"type":"text"},"year_founded":{"type":"double"}}}}"""
    }
    "apply template to new indexes that match the pattern" ignore {

      // this should match the earlier template of brew*
      client.execute {
        createIndex("brewers")
      }.await

      client.execute {
        indexInto("brewers" / "brands") fields(
          "name" -> "fullers",
          "year_founded" -> 1829
        ) refresh RefreshPolicy.IMMEDIATE
      }.await

      blockUntilCount(1, "brewers")

      // check that the document was indexed
      client.execute {
        search("brewers" / "brands") query termQuery("year_founded", 1829)
      }.await.totalHits shouldBe 1

      // the mapping for this index should match the template
      val properties = client.execute {
        getMapping("brewers" / "brands")
      }.await.propertiesFor("brewers" / "brands")

      val year_founded = properties("year_founded").asInstanceOf[util.Map[String, Any]]

      // note: this field would be long/int if the template wasn't applied, because we indexed an integer.
      // but the template should be applied to override it to a double
      year_founded.get("type") shouldBe "double"
    }
    "support template before any index creation" in {

      client.execute {
        createTemplate("malbec") pattern "malbec*" mappings (
          mapping("user") fields textField("distance")
          )
      }.await

      client.execute {
        indexInto("malbec" / "user") fields (
          "distance" -> 1234
          )
      }.await
    }
  }
}
