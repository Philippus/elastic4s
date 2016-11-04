package com.sksamuel.elastic4s.mappings

import java.util

import com.sksamuel.elastic4s.analyzers.{CustomAnalyzerDefinition, FrenchLanguageAnalyzer, LowercaseTokenFilter, WhitespaceAnalyzer, WhitespaceTokenizer}
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class MappingTest extends WordSpec with ElasticSugar with Matchers {

  client.execute {
    create index "q" mappings {
      mapping("r") as Seq(
        field("a", TextType) stored true analyzer WhitespaceAnalyzer,
        field("b", TextType)
      )
    } analysis {
      CustomAnalyzerDefinition("my_analyzer", WhitespaceTokenizer, LowercaseTokenFilter)
    }
  }.await

  "mapping get" should {
    "return schema" in {

      val mapping = client.execute {
        get mapping "q" / "r"
      }.await

      val map = mapping.mappings("q")("r").sourceAsMap()

      val a = map.get("properties").asInstanceOf[util.Map[String, Any]].get("a").asInstanceOf[util.Map[String, Any]]
      a.get("type") shouldBe "text"
      a.get("store") shouldBe true
      a.get("analyzer") shouldBe "whitespace"

      val b = map.get("properties").asInstanceOf[util.Map[String, Any]].get("b").asInstanceOf[util.Map[String, Any]]
      b.get("type") shouldBe "text"
    }
  }
  "mapping put" should {
    "add new fields" in {

      client.execute {
        put mapping "q" / "r" as Seq(
          field("c", FloatType) boost 1.2,
          field("d", TextType) analyzer FrenchLanguageAnalyzer
        )
      }.await

      val mapping = client.execute {
        get mapping "q" / "r"
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
