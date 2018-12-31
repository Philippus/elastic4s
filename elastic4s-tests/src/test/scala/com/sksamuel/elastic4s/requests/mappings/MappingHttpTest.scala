package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.requests.analyzers.{CustomAnalyzerDefinition, CustomNormalizerDefinition, LowercaseTokenFilter, WhitespaceAnalyzer, WhitespaceTokenizer}
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicMapping
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class MappingHttpTest extends WordSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("index")
    }.await

    client.execute {
      deleteIndex("indexnoprops")
    }.await
  }

  client.execute {
    createIndex("index").mappings(
      mapping("mapping1") as Seq(
        textField("a") stored true analyzer WhitespaceAnalyzer,
        keywordField("b") normalizer "my_normalizer",
        joinField("c") relation ("parent", Seq("bar", "foo"))
      )
    ) analysis {
      CustomAnalyzerDefinition("my_analyzer", WhitespaceTokenizer, LowercaseTokenFilter)
    } normalizers {
      CustomNormalizerDefinition("my_normalizer", LowercaseTokenFilter)
    }
  }.await


  client.execute {
    createIndex("indexnoprops").mappings(
      mapping("mapping2").dynamic(DynamicMapping.Strict)
    )
  }.await

  "mapping get" should {
    "return specified mapping" in {

      val mappings = client.execute {
        getMapping("index" / "mapping1")
      }.await.result

      val properties = mappings.find(_.index == "index").get.mappings("mapping1")
      val a = properties("a").asInstanceOf[Map[String, Any]]
      a("type") shouldBe "text"
      a("store") shouldBe true
      a("analyzer") shouldBe "whitespace"

      val b = properties("b").asInstanceOf[Map[String, Any]]
      b("type") shouldBe "keyword"
      b("normalizer") shouldBe "my_normalizer"

      val c = properties("c").asInstanceOf[Map[String, Any]]
      c("type") shouldBe "join"
      c("relations") shouldEqual Map("parent" -> Seq("bar", "foo"))
    }

    "handle properly mapping without properties" in {

      val mappings = client.execute {
        getMapping("indexnoprops" / "mapping2")
      }.await.result

      val properties = mappings.find(_.index == "indexnoprops").get.mappings("mapping2")

      properties shouldBe Map.empty
    }
  }
}
