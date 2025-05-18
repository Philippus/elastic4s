package com.sksamuel.elastic4s.requests.update

import java.util.UUID

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global

class UpdateTest
    extends AnyFlatSpec
    with Matchers
    with DockerTests
    with OptionValues {

  private val createMapping = createIndex("hans").mapping(
    properties(
      textField("name").stored(true)
    )
  )

  private val simpleIndex = indexInto("hans") fields "name" -> "intersteller" id "5" refresh RefreshPolicy.Immediate
  private val nestedIndex = indexInto("hans").fields(
    "recording_location" ->
      Map(
        "city"     -> "London",
        "country"  -> "United Kingdom",
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
      updateById("hans", "5").doc(
        "name" -> "man of steel"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("hans", "5").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("man of steel"))
  }

  it should "support nested field based update" in {
    val fieldName = "recording_location"
    val document  = Map(
      fieldName ->
        Map(
          "city"     -> "London!",
          "country"  -> "United Kingdom!",
          "position" -> List(-0.110146, 51.513176)
        )
    )
    client.execute {
      updateById("hans", "5").doc(document).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("hans", "5")
    }.await.result.sourceAsMap.get(fieldName).value shouldBe document.get(fieldName).value
  }

  it should "support string based update" in {
    client.execute {
      updateById("hans", "5").doc(""" { "name" : "inception" } """).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("hans", "5").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("inception"))
  }

  it should "support field based upsert" in {
    client.execute {
      updateById("hans", "5").docAsUpsert(
        "name" -> "batman"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("hans", "5").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "support string based upsert" in {
    client.execute {
      updateById("hans", "44").docAsUpsert(""" { "name" : "pirates of the caribbean" } """).refresh(
        RefreshPolicy.Immediate
      )
    }.await.result.result shouldBe "created"

    client.execute {
      get("hans", "44").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("pirates of the caribbean"))
  }

  it should "keep existing fields with partial update" in {

    client.execute {
      updateById("hans", "5").docAsUpsert(
        "length" -> 12.34
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.result shouldBe "updated"

    client.execute {
      get("hans", "5").storedFields("name")
    }.await.result.storedFieldsAsMap shouldBe Map("name" -> List("batman"))
  }

  it should "insert non existent doc when using docAsUpsert" in {
    client.execute {
      updateById("hans", "14").docAsUpsert(
        "name" -> "hunt for the red october"
      )
    }.await.result.result shouldBe "created"
  }

  it should "return errors when the index does not exist" in {
    val resp = client.execute {
      updateById("wowooasdsad", "5").doc(
        "name" -> "gladiator"
      )
    }.await
    resp.error.`type` shouldBe "document_missing_exception"
    resp.error.reason should include("document missing")
  }

  it should "return errors when the id does not exist" in {
    val resp = client.execute {
      updateById("hans", "234234").doc(
        "name" -> "gladiator"
      )
    }.await
    resp.error.`type` shouldBe "document_missing_exception"
    resp.error.reason should include("document missing")
  }

  it should "not return source by default" in {
    val resp = client.execute {
      updateById("hans", "666").docAsUpsert(
        "name" -> "dunkirk"
      ).refresh(RefreshPolicy.Immediate)
    }.await
    resp.result.source shouldBe Map.empty
  }

  it should "return source when specified" in {
    val resp = client.execute {
      updateById("hans", "667").docAsUpsert(
        "name" -> "thin red line"
      ).refresh(RefreshPolicy.Immediate).fetchSource(true)
    }.await
    resp.result.source shouldBe Map("name" -> "thin red line")
  }

  it should "include the original json" in {
    val resp = client.execute {
      updateById("hans", "555").docAsUpsert(
        "name" -> "spider man"
      ).refresh(RefreshPolicy.Immediate).fetchSource(true)
    }.await
    resp.body.get shouldBe """{"_index":"hans","_id":"555","_version":1,"result":"created","forced_refresh":true,"_shards":{"total":2,"successful":1,"failed":0},"_seq_no":11,"_primary_term":1,"get":{"_seq_no":11,"_primary_term":1,"found":true,"_source":{"name":"spider man"}}}"""
  }

  it should "handle concurrency with internal versioning" in {

    val id                     = UUID.randomUUID.toString
    val result                 = client.execute {
      updateById("hans", id).docAsUpsert(
        "name" -> "rain man"
      ).refresh(RefreshPolicy.Immediate)
    }.await
    val wrongPrimaryTermResult = client.execute {
      updateById("hans", id).doc(""" { "name" : "madagascar" } """).ifSeqNo(result.result.seqNo)
        .ifPrimaryTerm(result.result.primaryTerm + 1).refresh(RefreshPolicy.Immediate)
    }.await
    wrongPrimaryTermResult.error.toString should include("version_conflict_engine_exception")
    val wrongSeqNoResult       = client.execute {
      updateById("hans", id).doc(""" { "name" : "madagascar" } """).ifSeqNo(result.result.seqNo + 1)
        .ifPrimaryTerm(result.result.primaryTerm).refresh(RefreshPolicy.Immediate)
    }.await
    wrongSeqNoResult.error.toString should include("version_conflict_engine_exception")
    val successfulUpdateResult = client.execute {
      updateById("hans", id).doc(""" { "name" : "madagascar" } """).ifSeqNo(result.result.seqNo)
        .ifPrimaryTerm(result.result.primaryTerm).refresh(RefreshPolicy.Immediate)
    }.await
    successfulUpdateResult.isSuccess shouldBe true
  }
}
