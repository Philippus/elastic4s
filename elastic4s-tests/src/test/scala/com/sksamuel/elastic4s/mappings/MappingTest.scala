package com.sksamuel.elastic4s.mappings

import java.util

import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, ElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class MappingTest extends WordSpec with ElasticSugar with Matchers with ClassloaderLocalNodeProvider {

  client.execute {
    createIndex("q").mappings {
      mapping("r") as Seq(
        textField("a") stored true analyzer WhitespaceAnalyzer,
        textField("b"),
        keywordField("kw") normalizer "my_normalizer"
      )
    } analysis {
      CustomAnalyzerDefinition("my_analyzer", WhitespaceTokenizer, LowercaseTokenFilter)
    } normalizers {
      CustomNormalizerDefinition("my_normalizer", LowercaseTokenFilter)
    }
  }.await

  client.execute {
    createIndex("z") mappings(
      mapping("r"),
      mapping("s"),
      mapping("t")
    )
  }.await

  "mapping get" should {
    "return schema" in {

      val mapping = client.execute {
        getMapping("q" / "r")
      }.await

      val map = mapping.mappings("q")("r").sourceAsMap()

      val a = map.get("properties").asInstanceOf[util.Map[String, Any]].get("a").asInstanceOf[util.Map[String, Any]]
      a.get("type") shouldBe "text"
      a.get("store") shouldBe true
      a.get("analyzer") shouldBe "whitespace"

      val b = map.get("properties").asInstanceOf[util.Map[String, Any]].get("b").asInstanceOf[util.Map[String, Any]]
      b.get("type") shouldBe "text"

      val kw = map.get("properties").asInstanceOf[util.Map[String, Any]].get("kw").asInstanceOf[util.Map[String, Any]]
      kw.get("type") shouldBe "keyword"
      kw.get("normalizer") shouldBe "my_normalizer"
    }
    "support getting all mappings" in {
      client.execute {
        getMapping("z")
      }.await.mappingsFor("z").keySet shouldBe Set("r", "s", "t")
    }
  }
  "mapping put" should {
    "add new fields" in {

      client.execute {
        putMapping("q" / "r") as Seq(
          floatField("c") boost 1.2,
          textField("d") analyzer FrenchLanguageAnalyzer
        )
      }.await

      val mapping = client.execute {
        getMapping("q" / "r")
      }.await

      val map = mapping.mappings("q")("r").sourceAsMap()

      val c = map.get("properties").asInstanceOf[util.Map[String, _]].get("c").asInstanceOf[util.Map[String, _]]
      c.get("type") shouldBe "float"
      c.get("boost") shouldBe 1.2

      val d = map.get("properties").asInstanceOf[util.Map[String, _]].get("d").asInstanceOf[util.Map[String, _]]
      d.get("type") shouldBe "text"
      d.get("analyzer") shouldBe "french"
    }
  }
}
