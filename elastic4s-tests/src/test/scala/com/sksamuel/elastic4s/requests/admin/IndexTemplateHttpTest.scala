package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class IndexTemplateHttpTest
  extends WordSpec
    with MockitoSugar
    with Matchers
    with DockerTests {

  "create template" should {
    "be stored" in {
      client.execute {
        createIndexTemplate("brewery_template", "brew*").mappings(
          mapping("brands").fields(
            textField("name"),
            doubleField("year_founded")
          )
        )
      }.await.result.acknowledged shouldBe true
    }
    "be retrievable" in {
      val resp = client.execute {
        getIndexTemplate("brewery_template")
      }.await
      resp.result.templateFor("brewery_template").indexPatterns shouldBe Seq("brew*")
      resp.result.templateFor("brewery_template").order shouldBe 0
    }
    "return error if the template has invalid parameters" in {
      client.execute {
        createIndexTemplate("brewery_template", "brew*").mappings(
          mapping("brands").fields(
            textField("name"),
            doubleField("year_founded") analyzer "test_analyzer"
          )
        )
      }.await.error.`type` shouldBe "mapper_parsing_exception"
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
        ) refresh RefreshPolicy.Immediate
      }.await

      // check that the document was indexed
      client.execute {
        search("brewers") query termQuery("year_founded", 1829)
      }.await.result.totalHits shouldBe 1

      // the mapping for this index should match the template
      //   val properties = http.execute {
      //     getMapping("brewers" / "brands")
      //   }.await.propertiesFor("brewers" / "brands")

      //   val year_founded = properties("year_founded").asInstanceOf[util.Map[String, Any]]

      // note: this field would be long/int if the template wasn't applied, because we indexed an integer.
      // but the template should be applied to override it to a double
      //     year_founded.get("type") shouldBe "double"
    }
  }
}
