package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import com.sksamuel.elastic4s.{ElasticApi, RefreshPolicy}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class UpdateTest extends FlatSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      ElasticApi.deleteIndex("hans")
    }.await
  }

  http.execute {
    createIndex("hans").mappings(
      mapping("albums").fields(
        textField("name").stored(true)
      )
    )
  }.await

  http.execute(
    indexInto("hans/albums") fields "name" -> "intersteller" id 5 refresh RefreshPolicy.Immediate
  ).await

  "an update request" should "support field based update" in {

    http.execute {
      update(5).in("hans" / "albums").doc(
        "name" -> "man of steel"
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result shouldBe "updated"

    http.execute {
      get(5).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("man of steel"))
  }

  it should "support string based update" in {
    http.execute {
      update(5).in("hans" / "albums").doc(""" { "name" : "inception" } """).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result shouldBe "updated"

    http.execute {
      get(5).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("inception"))
  }

  it should "support field based upsert" in {

    http.execute {
      update(5).in("hans/albums").docAsUpsert(
        "name" -> "batman"
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result shouldBe "updated"

    http.execute {
      get(5).from("hans" / "albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "support string based upsert" in {
    http.execute {
      update(44).in("hans" / "albums").docAsUpsert(""" { "name" : "pirates of the caribbean" } """).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result shouldBe "created"

    http.execute {
      get(44).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("pirates of the caribbean"))
  }

  it should "keep existing fields with partial update" in {

    http.execute {
      update(5).in("hans/albums").docAsUpsert(
        "length" -> 12.34
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result shouldBe "updated"

    http.execute {
      get(5).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "insert non existent doc when using docAsUpsert" in {
    http.execute {
      update(14).in("hans/albums").docAsUpsert(
        "name" -> "hunt for the red october"
      )
    }.await.right.get.result shouldBe "created"
  }

  it should "return errors when the index does not exist" in {
    val resp = http.execute {
      update(5).in("wowooasdsad").doc(
        "name" -> "gladiator"
      )
    }.await
    resp.left.get.error.`type` shouldBe "document_missing_exception"
    resp.left.get.error.reason should include("document missing")
  }

  it should "return errors when the id does not exist" in {
    val resp = http.execute {
      update(234234).in("hans/albums").doc(
        "name" -> "gladiator"
      )
    }.await
    resp.left.get.error.`type` shouldBe "document_missing_exception"
    resp.left.get.error.reason should include("document missing")
  }
}
