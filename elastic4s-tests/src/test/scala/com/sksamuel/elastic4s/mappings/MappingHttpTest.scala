package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class MappingHttpTest extends WordSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {

    http.execute {
      deleteIndex("index")
    }.await
  }

  http.execute {
    createIndex("index").mappings(
      mapping("mapping1") as Seq(
        textField("a") stored true analyzer WhitespaceAnalyzer,
        keywordField("b") normalizer "my_normalizer"
      )
    ) analysis {
      CustomAnalyzerDefinition("my_analyzer", WhitespaceTokenizer, LowercaseTokenFilter)
    } normalizers {
      CustomNormalizerDefinition("my_normalizer", LowercaseTokenFilter)
    }
  }.await

  "mapping get" should {
    "return specified mapping" in {

      val mappings = http.execute {
        getMapping("index" / "mapping1")
      }.await.get

      val properties = mappings.find(_.index == "index").get.mappings("mapping1")
      val a = properties("a").asInstanceOf[Map[String, Any]]
      a("type") shouldBe "text"
      a("store") shouldBe true
      a("analyzer") shouldBe "whitespace"

      val b = properties("b").asInstanceOf[Map[String, Any]]
      b("type") shouldBe "keyword"
      b("normalizer") shouldBe "my_normalizer"
    }
  }
}
