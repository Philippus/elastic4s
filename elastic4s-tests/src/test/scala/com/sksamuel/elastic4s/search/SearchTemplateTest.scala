package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.script.StoredScriptSource
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class SearchTemplateTest extends AnyFlatSpec with Matchers with DockerTests {

  private val templateId = "elements-by-field"

  Try {
    client.execute {
      deleteStoredScript(templateId)
    }.await
  }

  deleteIdx("elements")

  client.execute {
    createIndex("elements").mapping(
      properties(
        keywordField("name"),
        textField("discoverer")
      )
    )
  }.await

  client.execute {
    bulk(
      indexInto("elements").fields("name" -> "Phosphorus", "discoverer" -> "Brand"),
      indexInto("elements").fields("name" -> "Cobalt", "discoverer"     -> "Brandt"),
      indexInto("elements").fields("name" -> "Hydrogen", "discoverer"   -> "Cavendish"),
      indexInto("elements").fields("name" -> "Potassium", "discoverer"  -> "Humphry Davy"),
      indexInto("elements").fields("name" -> "Barium", "discoverer"     -> "Scheele")
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "search template" should "be stored as a mustache stored script" in {
    client.execute {
      putStoredScript(
        templateId,
        StoredScriptSource("mustache", """{"query":{"match":{"{{myfield}}":"{{myterm}}"}}}""")
      )
    }.await.result.acknowledged shouldBe true

    client.execute {
      getStoredScript(templateId)
    }.await.result.script.lang shouldBe "mustache"
  }

  it should "use params when searching with the stored template" in {
    val resp = client.execute {
      templateSearch("elements").name(templateId).params(Map("myfield" -> "discoverer", "myterm" -> "Brand"))
    }.await.result

    resp.totalHits shouldBe 1
    resp.hits.hits.head.sourceAsMap("name") shouldBe "Phosphorus"
  }

  it should "be removed with the stored scripts api" in {
    client.execute {
      deleteStoredScript(templateId)
    }.await.result.acknowledged shouldBe true

    client.execute {
      getStoredScript(templateId)
    }.await.isError shouldBe true
  }
}
