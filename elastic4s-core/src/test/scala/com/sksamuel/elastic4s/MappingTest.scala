package com.sksamuel.elastic4s

import java.util

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType._
import org.scalatest.{ WordSpec, Matchers }
import org.scalatest.mock.MockitoSugar

/** @author Stephen Samuel */
class MappingTest extends WordSpec with MockitoSugar with ElasticSugar with Matchers {

  client.execute {
    create index "q" mappings {
      "r" as Seq(
        field name "a" withType StringType stored true analyzer WhitespaceAnalyzer,
        field name "b" withType StringType
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

      val map = mapping.mappings().get("q").get("r").sourceAsMap()

      val a = map.get("properties").asInstanceOf[util.Map[String, Any]].get("a").asInstanceOf[util.Map[String, Any]]
      a.get("type") shouldBe "string"
      a.get("store") shouldBe true
      a.get("analyzer") shouldBe "whitespace"

      val b = map.get("properties").asInstanceOf[util.Map[String, Any]].get("b").asInstanceOf[util.Map[String, Any]]
      b.get("type") shouldBe "string"
    }
  }
  "mapping put" should {
    "add new fields" in {

      client.execute {
        put mapping "q" / "r" as Seq(
          field name "c" withType FloatType boost 1.2,
          field name "d" withType StringType analyzer FrenchLanguageAnalyzer
        ) ignoreConflicts true
      }.await

      val mapping = client.execute {
        get mapping "q" / "r"
      }.await

      val map = mapping.mappings().get("q").get("r").sourceAsMap()

      val c = map.get("properties").asInstanceOf[util.Map[String, _]].get("c").asInstanceOf[util.Map[String, _]]
      c.get("type") shouldBe "float"
      c.get("boost") shouldBe 1.2

      val d = map.get("properties").asInstanceOf[util.Map[String, _]].get("d").asInstanceOf[util.Map[String, _]]
      d.get("type") shouldBe "string"
      d.get("analyzer") shouldBe "french"
    }
    "update existing fields" in {

      client.execute {
        put mapping "q" / "r" as Seq(
          field name "a" withType StringType boost 1.2,
          field name "b" withType StringType analyzer GermanLanguageAnalyzer
        ) ignoreConflicts true
      }.await

      val mapping = client.execute {
        get mapping "q" / "r"
      }.await

      val map = mapping.mappings().get("q").get("r").sourceAsMap()

      val a = map.get("properties").asInstanceOf[util.Map[String, _]].get("a").asInstanceOf[util.Map[String, _]]
      a.get("boost") shouldBe 1.2

      val b = map.get("properties").asInstanceOf[util.Map[String, _]].get("b").asInstanceOf[util.Map[String, _]]
      b.get("search_analyzer") shouldBe "german"
    }
  }
  "mapping delete" should {
    "remove mappings and data" in {

      client.execute {
        delete mapping "q" / "r"
      }.await

      val mapping = client.execute {
        get mapping "q" / "r"
      }.await

      mapping.mappings().isEmpty shouldBe true
    }
  }
}
