package com.sksamuel.elastic4s.requests.admin

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.indexes.CreateIndexTemplateRequest
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.util.Try

class IndexTemplateHttpTest
  extends AnyWordSpec
    with MockitoSugar
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

  "create template" should {
    "create template" in {

      val result = client.execute {
        CreateIndexTemplateRequest("brewery_template", "brew*").mappings(
          properties(
            textField("name").boost(123),
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
    "apply template to new indexes that match the pattern" in {

      // this should match the earlier template of brew*
      client.execute {
        createIndex("brewers")
      }.await

      client.execute {
        indexInto("brewers") fields(
          "name" -> "fullers",
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
