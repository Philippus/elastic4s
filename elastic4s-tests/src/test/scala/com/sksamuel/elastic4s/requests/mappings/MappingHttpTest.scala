package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.requests.analyzers._
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
      mapping() as Seq(
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
      mapping().dynamic(DynamicMapping.Strict)
    )
  }.await

  "mapping get" should {
    "return specified mapping" in {

      val mappings = client.execute {
        getMapping("index")
      }.await.result

      val properties = mappings.find(_.index == "index").get.mappings
      println(mappings)
      val a = properties
      a("type") shouldBe "text"
      a("store") shouldBe true
      a("analyzer") shouldBe "whitespace"

      val b = properties
      b("type") shouldBe "keyword"
      b("normalizer") shouldBe "my_normalizer"

      val c = properties
      c("type") shouldBe "join"
      c("relations") shouldEqual Map("parent" -> Seq("bar", "foo"))
    }

    "handle properly mapping without properties" in {

      val mappings = client.execute {
        getMapping("indexnoprops")
      }.await.result

      val properties = mappings.find(_.index == "indexnoprops").get.mappings

      properties shouldBe Map.empty
    }
  }
}
