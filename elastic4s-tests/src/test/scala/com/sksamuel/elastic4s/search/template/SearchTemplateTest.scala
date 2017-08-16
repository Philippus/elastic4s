package com.sksamuel.elastic4s.search.template

import com.sksamuel.elastic4s.{ElasticsearchClientUri, RefreshPolicy}
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

class SearchTemplateTest extends FlatSpec with ElasticDsl with DiscoveryLocalNodeProvider with Matchers {

  http.execute {
    bulk(
      indexInto("searchtemplate/landmarks").fields("name" -> "hampton court palace"),
      indexInto("searchtemplate/landmarks").fields("name" -> "tower of london"),
      indexInto("searchtemplate/landmarks").fields("name" -> "stonehenge"),
      indexInto("searchtemplate/landmarks").fields("name" -> "tower bridge")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a search template" should "be puttable and gettable" in {

    http.execute {
      putSearchTemplate("testy", matchQuery("{{field}}", "{{text}}"))
    }.await.acknowledged shouldBe true

    http.execute {
      getSearchTemplate("testy")
    }.await.get.id shouldBe "testy"
  }

  it should "be usable in a search" in {
    val result = http.execute {
      templateSearch("searchtemplate").name("testy").params(Map("field" -> "name", "text" -> "tower"))
    }.await
    result.totalHits shouldBe 2
    result.hits.hits.map(_.sourceAsString).toSet shouldBe Set(
      """{"name":"tower bridge"}""",
      """{"name":"tower of london"}"""
    )
  }

  it should "be deletable" in {

    http.execute {
      removeSearchTemplate("testy")
    }.await.acknowledged shouldBe true

    http.execute {
      getSearchTemplate("testy")
    }.await shouldBe None
  }
}
