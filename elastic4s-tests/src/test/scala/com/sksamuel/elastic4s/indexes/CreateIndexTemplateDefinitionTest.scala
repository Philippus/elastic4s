package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.{ElasticApi, ElasticsearchClientUri}
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.ClassloaderLocalNodeProvider
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpec}

import scala.util.Try

class CreateIndexTemplateDefinitionTest extends WordSpec
  with Matchers
  with BeforeAndAfter
  with BeforeAndAfterAll
  with ElasticApi
  with ElasticDsl
  with ClassloaderLocalNodeProvider  {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + getNode.ipAndPort))

  before {
    Try {
      http.execute {
        deleteIndex("matchme.template")
      }.await
    }
  }

  override def afterAll(): Unit = {
    http.close()
  }

  "Create Index Template HTTP request" should {
    "create and use the template for an index" in {
      http.execute {
        createTemplate("matchme.*").pattern("matchme.*").mappings(
          mapping("sometype1").fields(
            keywordField("field1"),
            geopointField("field2"),
            keywordField("field3"),
            intField("field4")
          )
        )
      }.await

      http.execute {
        createIndex("matchme.template").shards(1).waitForActiveShards(1)
      }.await

      val resp = http.execute {
        getMapping("matchme.template")
      }.await

      resp.map(_.index) shouldBe Seq("matchme.template")
      resp.head.mappings.keySet shouldBe Set("sometype1")
      resp.head.mappings("sometype1").keySet shouldBe Set("field1", "field2", "field3", "field4")
    }
  }
}
