package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers, OptionValues}

import scala.concurrent.ExecutionContext.Implicits.global

class UpdateTest
  extends FlatSpec
    with Matchers
    with DockerTests
    with OptionValues {

  private val createMapping = createIndex("hans").mappings(
    mapping("albums").fields(
      textField("name").stored(true)
    )
  )

  private val simpleIndex = indexInto("hans/albums") fields "name" -> "intersteller" id "5" refresh RefreshPolicy.Immediate
  private val nestedIndex = indexInto("hans/albums").fields(
    "recording_location" ->
      Map(
        "city" -> "London",
        "country" -> "United Kingdom",
        "position" -> List(-0.127413, 51.506907)
      )
  ) id "5" refresh RefreshPolicy.Immediate

  private val idxRequests = for {
    _ <- client.execute(ElasticApi.deleteIndex("hans"))
    _ <- client.execute(createMapping)
    _ <- client.execute(simpleIndex)
    _ <- client.execute(nestedIndex)
  } yield ()
  idxRequests.await

  "an update request" should "support field based update" in {
    client.execute {
      update("5").in("hans" / "albums").doc(
        "name" -> "man of steel"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("5").from("hans/albums").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("man of steel"))
  }

  it should "support nested field based update" in {
    val fieldName = "recording_location"
    val document = Map(
      fieldName ->
        Map(
          "city" -> "London!",
          "country" -> "United Kingdom!",
          "position" -> List(-0.110146, 51.513176)
        )
    )
    client.execute {
      update("5").in("hans" / "albums").doc(document).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("5").from("hans/albums")
    }.await.result.sourceAsMap.get(fieldName).value shouldBe document.get(fieldName).value
  }

  it should "support string based update" in {
    client.execute {
      update("5").in("hans" / "albums").doc(""" { "name" : "inception" } """).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("5").from("hans/albums").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("inception"))
  }

  it should "support field based upsert" in {
    client.execute {
      update("5").in("hans/albums").docAsUpsert(
        "name" -> "batman"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("5").from("hans" / "albums").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "support string based upsert" in {
    client.execute {
      update("44").in("hans" / "albums").docAsUpsert(""" { "name" : "pirates of the caribbean" } """).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "created"

    client.execute {
      get("44").from("hans/albums").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("pirates of the caribbean"))
  }

  it should "keep existing fields with partial update" in {

    client.execute {
      update("5").in("hans/albums").docAsUpsert(
        "length" -> 12.34
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("5").from("hans/albums").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "insert non existent doc when using docAsUpsert" in {
    client.execute {
      update("14").in("hans/albums").docAsUpsert(
        "name" -> "hunt for the red october"
      )
    }.await.result.result shouldBe "created"
  }

  it should "return errors when the index does not exist" in {
    val resp = client.execute {
      update("5").in("wowooasdsad" / "qweqwe").doc(
        "name" -> "gladiator"
      )
    }.await
    resp.error.`type` shouldBe "document_missing_exception"
    resp.error.reason should include("document missing")
  }

  it should "return errors when the id does not exist" in {
    val resp = client.execute {
      update("234234").in("hans/albums").doc(
        "name" -> "gladiator"
      )
    }.await
    resp.error.`type` shouldBe "document_missing_exception"
    resp.error.reason should include("document missing")
  }

  it should "not return source by default" in {
    val resp = client.execute {
      update("666").in("hans/albums").docAsUpsert(
        "name" -> "dunkirk"
      ).refresh(RefreshPolicy.Immediate)
    }.await
    resp.result.source shouldBe Map.empty
  }

  it should "return source when specified" in {
    val resp = client.execute {
      update("667").in("hans/albums").docAsUpsert(
        "name" -> "thin red line"
      ).refresh(RefreshPolicy.Immediate).fetchSource(true)
    }.await
    resp.result.source shouldBe Map("name" -> "thin red line")
  }

  it should "include the original json" in {
    val resp = client.execute {
      update("555").in("hans/albums").docAsUpsert(
        "name" -> "spider man"
      ).refresh(RefreshPolicy.Immediate).fetchSource(true)
    }.await
    resp.body.get shouldBe """{"_index":"hans","_type":"albums","_id":"555","_version":1,"result":"created","forced_refresh":true,"_shards":{"total":2,"successful":1,"failed":0},"_seq_no":11,"_primary_term":1,"get":{"found":true,"_source":{"name":"spider man"}}}"""
  }
}
