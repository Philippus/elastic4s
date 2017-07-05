package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.index.{Field, Mapping}
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class GetIndexTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      deleteIndex("getindextest")
    }.await
  }

  http.execute {
    createIndex("getindextest").mappings(
      mapping("mytype").fields(
        textField("a"),
        keywordField("b"),
        longField("c")
      )
    ).settings(Map("number_of_replicas" -> 2))
  }.await

  "get index" should {

    "return mapping info" in {
      val resp = http.execute {
        getIndex("getindextest")
      }.await
      resp("getindextest").mappings shouldBe Map("mytype" -> Mapping(Map("a" -> Field("text"), "b" -> Field("keyword"), "c" -> Field("long"))))
    }

    "return settings" in {
      val resp = http.execute {
        getIndex("getindextest")
      }.await

      resp("getindextest").settings("index.number_of_shards") shouldBe "5"
      resp("getindextest").settings("index.number_of_replicas") shouldBe "2"
      resp("getindextest").settings("index.provided_name") shouldBe "getindextest"
    }

    "return aliases" in {

      http.execute {
        addAlias("myalias1").on("getindextest")
      }.await

      http.execute {
        addAlias("myalias2").on("getindextest")
      }.await

      val resp = http.execute {
        getIndex("getindextest")
      }.await

      resp("getindextest").aliases.keySet shouldBe Set("myalias1", "myalias2")
    }
  }
}
