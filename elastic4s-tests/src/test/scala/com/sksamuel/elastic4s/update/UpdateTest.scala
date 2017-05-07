package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class UpdateTest extends FlatSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  override protected def beforeRunTests() = {
    execute {
      createIndex("hans").mappings(
        mapping("albums").fields(
          textField("name").stored(true)
        )
      )
    }.await

    execute(
      indexInto("hans/albums") fields "name" -> "intersteller" id 5
    ).await

    blockUntilCount(1, "hans")
  }

  "an update request" should "support field based update" in {

    execute {
      update(5).in("hans" / "albums").doc(
        "name" -> "man of steel"
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "updated"

    execute {
      get(5).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("man of steel"))
  }

  it should "support string based update" in {
    execute {
      update(5).in("hans" / "albums").doc(""" { "name" : "inception" } """).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "updated"

    execute {
      get(5).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("inception"))
  }

  it should "support field based upsert" in {

    execute {
      update(5).in("hans/albums").docAsUpsert(
        "name" -> "batman"
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "updated"

    execute {
      get(5).from("hans" / "albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "support string based upsert" in {
    execute {
      update(44).in("hans" / "albums").docAsUpsert(""" { "name" : "pirates of the caribbean" } """).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "created"

    execute {
      get(44).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("pirates of the caribbean"))
  }

  it should "keep existing fields with partial update" in {

    execute {
      update(5).in("hans/albums").docAsUpsert(
        "length" -> 12.34
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "updated"

    execute {
      get(5).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "insert non existent doc when using docAsUpsert" in {
    execute {
      update(14).in("hans/albums").docAsUpsert(
        "name" -> "hunt for the red october"
      )
    }.await.result shouldBe "created"
  }

  it should "not insert doc when doc doesn't exist" in {
    val e = intercept[Exception] {
      execute {
        update(234234).in("hans/albums").doc(
          "name" -> "gladiator"
        )
      }.await
    }
    assert(e != null)
  }
}
