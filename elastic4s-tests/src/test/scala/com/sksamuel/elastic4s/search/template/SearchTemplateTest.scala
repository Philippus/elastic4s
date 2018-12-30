package com.sksamuel.elastic4s.search.template

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class SearchTemplateTest extends FlatSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("searchtemplate")
    }.await
  }

  client.execute {
    bulk(
      indexInto("searchtemplate/landmarks").fields("name" -> "hampton court palace"),
      indexInto("searchtemplate/landmarks").fields("name" -> "tower of london"),
      indexInto("searchtemplate/landmarks").fields("name" -> "stonehenge"),
      indexInto("searchtemplate/landmarks").fields("name" -> "tower bridge")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a search template" should "be puttable and gettable" in {

    client.execute {
      putSearchTemplate("testy", matchQuery("{{field}}", "{{text}}"))
    }.await.result.acknowledged shouldBe true

    client.execute {
      getSearchTemplate("testy")
    }.await.result.get.id shouldBe "testy"
  }

  it should "be usable in a search" in {
    val result = client.execute {
      templateSearch("searchtemplate").name("testy").params(Map("field" -> "name", "text" -> "tower"))
    }.await.result
    result.totalHits shouldBe 2
    result.hits.hits.map(_.sourceAsString).toSet shouldBe Set(
      """{"name":"tower bridge"}""",
      """{"name":"tower of london"}"""
    )
  }

  it should "be deletable" in {

    client.execute {
      removeSearchTemplate("testy")
    }.await.result.acknowledged shouldBe true

    client.execute {
      getSearchTemplate("testy")
    }.await.result shouldBe None
  }
}
