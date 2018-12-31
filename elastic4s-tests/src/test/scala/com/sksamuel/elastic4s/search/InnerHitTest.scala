package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.requests.mappings.Child
import com.sksamuel.elastic4s.requests.searches.{InnerHits, Total}
import com.sksamuel.elastic4s.requests.searches.queries.InnerHit
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

class InnerHitTest extends WordSpec with Matchers with DockerTests {

  val indexName = "inner_hit_test"
  deleteIdx(indexName)

  client.execute {
    createIndex(indexName).mappings {
      mapping("football").fields(
        keywordField("name"),
        joinField("affiliation").relation("club", "player")
      )
    }
  }.await

  client.execute {
    bulk(
      indexInto(indexName / "football").fields(Map("name" -> "boro", "affiliation" -> "club")).id("1").routing("1"),
      indexInto(indexName / "football").fields(Map("name" -> "traore", "affiliation" -> Child("player", "1"))).id("2").routing("1")
    ).refreshImmediately
  }.await

  "InnerHits" should {
    "we" in {
      val result = client.execute {
        search(indexName).query {
          hasChildQuery("player", matchAllQuery()).innerHit(InnerHit("myinner"))
        }
      }.await.result
      result.totalHits shouldBe 1
      result.hits.hits.head.innerHits shouldBe Map(
        "myinner" -> InnerHits(
          Total(1, "eq"),
          Some(1.0),
          List(
            com.sksamuel.elastic4s.requests.searches.InnerHit(
              indexName,
              "football",
              "2",
              Map.empty,
              Some(1.0),
              "1",
              Map("name" -> "traore", "affiliation" -> Map("name" -> "player", "parent" -> "1")),
              Map.empty,
              Map.empty,
              Nil,
              Map.empty
            )
          )
        )
      )
    }
  }

  "InnerHit" should {
    "include requested doc value fields" in {
      val result = client.execute {
        search(indexName).query {
          hasChildQuery("player", matchAllQuery())
            .innerHit(InnerHit("myinner").docValueFields(Set("name")))
        }
      }.await.result

      val innerHit = result.hits.hits.head.innerHits("myinner").hits.head
      innerHit.fields shouldBe Map("name" -> List("traore"))
      innerHit.docValueField("name").value shouldBe "traore"
      innerHit.docValueField("name").values shouldBe List("traore")
      innerHit.docValueFieldOpt("name") shouldBe defined
      innerHit.docValueFieldOpt("affiliation") shouldBe empty
    }
  }

}
