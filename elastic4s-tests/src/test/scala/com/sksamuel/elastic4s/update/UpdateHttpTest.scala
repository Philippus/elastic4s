package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class UpdateHttpTest extends FlatSpec with SharedElasticSugar with Matchers with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("hans").mappings(
      mapping("albums").fields(
        textField("name").stored(true)
      )
    )
  }.await

  http.execute(
    indexInto("hans/albums") fields "name" -> "intersteller" id 5
  ).await

  blockUntilCount(1, "hans")

  "an update request" should "support field based update" in {

    http.execute {
      update(5).in("hans" / "albums").doc(
        "name" -> "man of steel"
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "updated"

    http.execute {
      get(5).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("man of steel"))
  }

  it should "support string based update" in {
    http.execute {
      update(5).in("hans" / "albums").doc(""" { "name" : "inception" } """).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "updated"

    http.execute {
      get(5).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("inception"))
  }

  it should "support field based upsert" in {

    http.execute {
      update(5).in("hans/albums").docAsUpsert(
        "name" -> "batman"
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "updated"

    http.execute {
      get(5).from("hans" / "albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "support string based upsert" in {
    http.execute {
      update(44).in("hans" / "albums").docAsUpsert(""" { "name" : "pirates of the caribbean" } """).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "created"

    http.execute {
      get(44).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("pirates of the caribbean"))
  }

  it should "keep existing fields with partial update" in {

    http.execute {
      update(5).in("hans/albums").docAsUpsert(
        "length" -> 12.34
      ).refresh(RefreshPolicy.IMMEDIATE)
    }.await.result shouldBe "updated"

    http.execute {
      get(5).from("hans/albums").storedFields("name")
    }.await.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "insert non existent doc when using docAsUpsert" in {
    http.execute {
      update(14).in("hans/albums").docAsUpsert(
        "name" -> "hunt for the red october"
      )
    }.await.result shouldBe "created"
  }

  it should "not insert doc when doc doesn't exist" ignore {
    http.execute {
      update(234234).in("hans/albums").doc(
        "name" -> "gladiator"
      )
    }.await
  }
}
