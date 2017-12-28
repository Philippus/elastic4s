package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import com.sksamuel.elastic4s.{ElasticApi, RefreshPolicy}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

import scala.concurrent.ExecutionContext.Implicits.global

class UpdateTest extends FlatSpec
  with Matchers
  with ElasticDsl
  with DiscoveryLocalNodeProvider
  with OptionValues {

  val createMapping = createIndex("hans").mappings(
    mapping("albums").fields(
      textField("name").stored(true)
    )
  )
  val simpleIndex = indexInto("hans/albums") fields "name" -> "intersteller" id "5" refresh RefreshPolicy.Immediate
  val nestedIndex = indexInto("hans/albums").fields(
    "recording_location" ->
      Map(
        "city" -> "London",
        "country" -> "United Kingdom",
        "position" -> List(-0.127413,51.506907)
      )
  ) id "5" refresh RefreshPolicy.Immediate

  val idxRequests = for {
    _ <- http.execute(ElasticApi.deleteIndex("hans"))
    _ <- http.execute(createMapping)
    _ <- http.execute(simpleIndex)
    _ <- http.execute(nestedIndex)
  } yield ()
  idxRequests.await

  "an update request" should "support field based update" in {
    http.execute {
      update("5").in("hans" / "albums").doc(
        "name" -> "man of steel"
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.result shouldBe "updated"

    http.execute {
      get("5").from("hans/albums").storedFields("name")
    }.await.right.get.result.storedFieldsAsMap shouldBe Map("name" -> List("man of steel"))
  }

  it should "support nested field based update" in {
    val fieldName = "recording_location"
    val document = Map(
      fieldName ->
        Map(
          "city" -> "London!",
          "country" -> "United Kingdom!",
          "position" -> List(-0.110146,51.513176)
        )
    )
    http.execute {
      update("5").in("hans" / "albums").doc(document).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.result shouldBe "updated"

    http.execute {
      get("5").from("hans/albums")
    }.await.right.get.result.sourceAsMap.get(fieldName).value shouldBe document.get(fieldName).value
  }

  it should "support string based update" in {
    http.execute {
      update("5").in("hans" / "albums").doc(""" { "name" : "inception" } """).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.result shouldBe "updated"

    http.execute {
      get("5").from("hans/albums").storedFields("name")
    }.await.right.get.result.storedFieldsAsMap shouldBe Map("name" -> List("inception"))
  }

  it should "support field based upsert" in {
    http.execute {
      update("5").in("hans/albums").docAsUpsert(
        "name" -> "batman"
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.result shouldBe "updated"

    http.execute {
      get("5").from("hans" / "albums").storedFields("name")
    }.await.right.get.result.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "support string based upsert" in {
    http.execute {
      update("44").in("hans" / "albums").docAsUpsert(""" { "name" : "pirates of the caribbean" } """).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.result shouldBe "created"

    http.execute {
      get("44").from("hans/albums").storedFields("name")
    }.await.right.get.result.storedFieldsAsMap shouldBe Map("name" -> List("pirates of the caribbean"))
  }

  it should "keep existing fields with partial update" in {

    http.execute {
      update("5").in("hans/albums").docAsUpsert(
        "length" -> 12.34
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.result shouldBe "updated"

    http.execute {
      get("5").from("hans/albums").storedFields("name")
    }.await.right.get.result.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "insert non existent doc when using docAsUpsert" in {
    http.execute {
      update("14").in("hans/albums").docAsUpsert(
        "name" -> "hunt for the red october"
      )
    }.await.right.get.result.result shouldBe "created"
  }

  it should "return errors when the index does not exist" in {
    val resp = http.execute {
      update("5").in("wowooasdsad" / "qweqwe").doc(
        "name" -> "gladiator"
      )
    }.await
    resp.left.get.error.`type` shouldBe "document_missing_exception"
    resp.left.get.error.reason should include("document missing")
  }

  it should "return errors when the id does not exist" in {
    val resp = http.execute {
      update("234234").in("hans/albums").doc(
        "name" -> "gladiator"
      )
    }.await
    resp.left.get.error.`type` shouldBe "document_missing_exception"
    resp.left.get.error.reason should include("document missing")
  }

  it should "not return source by default" in {
    val resp = http.execute {
      update("666").in("hans/albums").docAsUpsert(
        "name" -> "dunkirk"
      ).refresh(RefreshPolicy.Immediate)
    }.await
    resp.right.get.result.source shouldBe Map.empty
  }

  it should "return source when specified" in {
    val resp = http.execute {
      update("667").in("hans/albums").docAsUpsert(
        "name" -> "thin red line"
      ).refresh(RefreshPolicy.Immediate).fetchSource(true)
    }.await
    resp.right.get.result.source shouldBe Map("name" -> "thin red line")
  }

  it should "include the original json" in {
    val resp = http.execute {
      update("555").in("hans/albums").docAsUpsert(
        "name" -> "spider man"
      ).refresh(RefreshPolicy.Immediate).fetchSource(true)
    }.await
    resp.right.get.body.get shouldBe """{"_index":"hans","_type":"albums","_id":"555","_version":1,"result":"created","forced_refresh":true,"_shards":{"total":2,"successful":1,"failed":0},"_seq_no":3,"_primary_term":1,"get":{"found":true,"_source":{"name":"spider man"}}}"""
  }
}
