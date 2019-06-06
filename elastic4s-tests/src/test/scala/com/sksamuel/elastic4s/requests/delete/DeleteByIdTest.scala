package com.sksamuel.elastic4s.requests.delete

import java.util.UUID

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.common.VersionType.Internal
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class DeleteByIdTest extends WordSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("lecarre")
    }.await
  }

  client.execute {
    createIndex("lecarre").mappings(
      mapping().fields(
        textField("name")
      )
    ).shards(1).waitForActiveShards(1)
  }.await

  "delete by id request" should {
    "delete matched docs" in {

      client.execute {
        indexInto("lecarre").fields("name" -> "jonathon pine").id("2").refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        indexInto("lecarre").fields("name" -> "george smiley").id("4").refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search("lecarre").matchAllQuery()
      }.await.result.totalHits shouldBe 2

      client.execute {
        delete("2").from("lecarre").refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search("lecarre").matchAllQuery()
      }.await.result.totalHits shouldBe 1

      client.execute {
        delete("4").from("lecarre").refresh(RefreshPolicy.Immediate)
      }.await

      client.execute {
        search("lecarre").matchAllQuery()
      }.await.result.totalHits shouldBe 0
    }

    "handle delete concurrency with internal versioning" in {

      val id = UUID.randomUUID.toString
      val insertResult = client.execute {
        indexInto("lecarre")
          .fields("name" -> "george smiley")
          .withId(id)
          .versionType(Internal)
          .refresh(RefreshPolicy.Immediate)
      }.await
      val wrongPrimaryTermResult = client.execute {
        delete(id).from("lecarre").ifSeqNo(insertResult.result.seqNo).
          ifPrimaryTerm(insertResult.result.primaryTerm + 1).versionType(Internal).refresh(RefreshPolicy.Immediate)
      }.await
      wrongPrimaryTermResult.error.toString should include ("version_conflict_engine_exception")
      val wrongSeqNoResult = client.execute {
        delete(id).from("lecarre").ifSeqNo(insertResult.result.seqNo + 1).
          ifPrimaryTerm(insertResult.result.primaryTerm).versionType(Internal).refresh(RefreshPolicy.Immediate)
      }.await
      wrongSeqNoResult.error.toString should include ("version_conflict_engine_exception")
      val successfulDeleteResult = client.execute {
        delete(id).from("lecarre").ifSeqNo(insertResult.result.seqNo).
          ifPrimaryTerm(insertResult.result.primaryTerm).versionType(Internal).refresh(RefreshPolicy.Immediate)
      }.await
      successfulDeleteResult.isSuccess shouldBe true
    }
  }
}
