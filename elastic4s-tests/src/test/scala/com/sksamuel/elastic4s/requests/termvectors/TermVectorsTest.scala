package com.sksamuel.elastic4s.requests.termvectors

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class TermVectorsTest extends FlatSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("termvecs")
    }.await
  }

  client.execute {
    createIndex("termvecs").mappings(
      mapping("albums").fields(
        textField("name").stored(true).termVector("with_positions_offsets_payloads"),
        intField("rating")
      )
    )
  }.await

  client.execute(
    bulk(
      indexInto("termvecs/albums").fields("name" -> "interstellar", "rating" -> 10) id "1",
      indexInto("termvecs/albums").fields("name" -> "lion king", "rating" -> 8) id "2"
    ).refresh(RefreshPolicy.Immediate)
  ).await

  client.execute {
    count(Indexes("termvecs"))
  }.await.result.count shouldBe 2

  "term vectors" should "return full stats" in {

    val response = client.execute {
      termVectors("termvecs", "albums", "1")
    }.await.result

    response.index shouldBe "termvecs"
    response.`type` shouldBe "albums"
    response.id shouldBe "1"
    response.found shouldBe true
    response.termVectors("name").fieldStatistics shouldBe FieldStatistics(3, 2, 3)
  }

  "multi term vectors" should "return full stats in multiple docs" in {

    val response = client.execute {
      multiTermVectors(
        termVectors("termvecs", "albums", "1"),
        termVectors("termvecs", "albums", "2")
      )
    }.await.result

    val docs = response.docs.sortBy(_.id)
    docs.size shouldBe 2

    docs.head.index shouldBe "termvecs"
    docs.head.`type` shouldBe "albums"
    docs.head.id shouldBe "1"
    docs.head.found shouldBe true
    docs.head.termVectors("name").fieldStatistics shouldBe FieldStatistics(3, 2, 3)

    docs(1).index shouldBe "termvecs"
    docs(1).`type` shouldBe "albums"
    docs(1).id shouldBe "2"
    docs(1).found shouldBe true
    docs(1).termVectors("name").fieldStatistics shouldBe FieldStatistics(3, 2, 3)
  }
}
