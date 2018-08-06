package com.sksamuel.elastic4s.termvectors

import com.sksamuel.elastic4s.http.termvectors._
import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class TermVectorsTest extends FlatSpec with Matchers with DockerTests {

  client.execute {
    createIndex("hansz").mappings(
      mapping("albums").fields(
        textField("name").stored(true).termVector("with_positions_offsets_payloads"),
        intField("rating")
      )
    )
  }.await

  client.execute(
    bulk(
      indexInto("hansz/albums").fields("name" -> "interstellar", "rating" -> 10) id "1",
      indexInto("hansz/albums").fields("name" -> "lion king", "rating" -> 8) id "2"
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "term vectors" should "return full stats" in {
    val response = client.execute {
      termVectors("hansz", "albums", "1")
    }.await.result

    response.index shouldBe "hansz"
    response.`type` shouldBe "albums"
    response.id shouldBe "1"
    response.found shouldBe true
    response.termVectors shouldBe Map("name" -> TermVectors(FieldStatistics(1, 1, 1), Map("interstellar" -> Terms(0, 0, 1.0, 1, List(Token(0, 0, 12))))))
  }

  "multi term vectors" should "return full stats in multiple docs" in {
    val response = client.execute {
      multiTermVectors(
        termVectors("hansz", "albums", "1"),
        termVectors("hansz", "albums", "2")
      )
    }.await.result

    val docs = response.docs.sortBy(_.id)
    docs.size shouldBe 2

    docs(0).index shouldBe "hansz"
    docs(0).`type` shouldBe "albums"
    docs(0).id shouldBe "1"
    docs(0).found shouldBe true
    docs(0).termVectors shouldBe Map("name" -> TermVectors(FieldStatistics(1, 1, 1), Map("interstellar" -> Terms(0, 0, 1.0, 1, List(Token(0, 0, 12))))))

    docs(1).index shouldBe "hansz"
    docs(1).`type` shouldBe "albums"
    docs(1).id shouldBe "2"
    docs(1).found shouldBe true
    docs(1).termVectors shouldBe Map("name" -> TermVectors(FieldStatistics(2, 1, 2), Map("lion" -> Terms(0, 0, 1.0, 1, List(Token(0, 0, 4))), "king" -> Terms(0, 0, 1.0, 1, List(Token(1, 5, 9))))))
  }
}
