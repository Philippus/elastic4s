package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.requests.analyzers._
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicMapping
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.util.Try

class MappingHttpTest extends WordSpec with DockerTests with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    Try {
      client.execute {
        deleteIndex("index")
      }.await

      client.execute {
        deleteIndex("indexnoprops")
      }.await

      client.execute {
        deleteIndex("indexnopropsempty")
      }.await
    }

    client.execute {
      createIndex("index").mappings(
        mapping() as Seq(
          textField("a") stored true analyzer WhitespaceAnalyzer,
          keywordField("b") normalizer "my_normalizer",
          joinField("c") relation("parent", Seq("bar", "foo"))
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

    client.execute {
      createIndex("indexnopropsempty").mappings(
        mapping()
      )
    }.await
  }

  "mapping get" should {
    "return specified mapping" in {

      val mappings = client.execute {
        getMapping("index")
      }.await.result

      val properties = mappings.find(_.index == "index").get.mappings
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
        getMapping("indexnoprops")
      }.await.result

      val properties = mappings.find(_.index == "indexnoprops").get.mappings

      properties shouldBe Map.empty
    }

    "handle properly completely empty mapping" in {

      val mappings = client.execute {
        getMapping("indexnopropsempty")
      }.await.result

      val properties = mappings.find(_.index == "indexnopropsempty").get.mappings

      properties shouldBe Map.empty
    }
  }

  "field mapping get" should {
    "return specified mapping" in {
      val mappings = client.execute {
        getMapping("index", "a")
      }.await.result

      val fieldMappings = mappings.find(_.index == "index").get.fieldMappings
      fieldMappings.size shouldBe 1
      fieldMappings.head.fullName shouldBe "a"

      val aField = fieldMappings.head.mappings("a").asInstanceOf[Map[String, Any]]
      aField("type") shouldBe "text"
      aField("store") shouldBe true
      aField("analyzer") shouldBe "whitespace"
    }

    "handle properly mapping without properties" in {

      val mappings = client.execute {
        getMapping("indexnoprops", "a")
      }.await.result

      val properties = mappings.find(_.index == "indexnoprops").get.fieldMappings

      properties shouldBe List.empty
    }

    "handle properly completely empty mapping" in {

      val mappings = client.execute {
        getMapping("indexnopropsempty", "a")
      }.await.result

      val properties = mappings.find(_.index == "indexnopropsempty").get.fieldMappings

      properties shouldBe List.empty
    }
  }
}
