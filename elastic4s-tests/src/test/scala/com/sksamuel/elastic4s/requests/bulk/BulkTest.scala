package com.sksamuel.elastic4s.requests.bulk

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.common.VersionType.Internal
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class BulkTest extends FlatSpec with Matchers with DockerTests {

  private val indexname = "bulkytest"

  Try {
    client.execute {
      deleteIndex(indexname)
    }.await
  }

  client.execute {
    createIndex(indexname).mapping {
      properties(
        intField("atomicweight").stored(true),
        textField("name").stored(true)
      )
    }
  }.await

  "bulk request" should "handle multiple index operations" in {

    client.execute {
      bulk(
        indexInto(indexname) fields("atomicweight" -> 2, "name" -> "helium") id "2",
        indexInto(indexname) fields("atomicweight" -> 4, "name" -> "lithium") id "4"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    client.execute {
      get("2").from(indexname)
    }.await.result.found shouldBe true

    client.execute {
      get("4").from(indexname)
    }.await.result.found shouldBe true
  }

  it should "return details of which items succeeded and failed" in {
    val result = client.execute {
      bulk(
        update("2").in(indexname).doc("atomicweight" -> 2, "name" -> "helium"),
        indexInto(indexname).fields("atomicweight" -> 8, "name" -> "oxygen") id "8",
        update("6").in(indexname).doc("atomicweight" -> 4, "name" -> "lithium"),
        delete("10").from(indexname)
      ).refresh(RefreshPolicy.Immediate)
    }.await.result

    result.hasFailures shouldBe true
    result.hasSuccesses shouldBe true
    result.errors shouldBe true

    result.failures.map(_.itemId).toSet shouldBe Set(2, 3)
    result.successes.map(_.itemId).toSet shouldBe Set(0, 1)
  }

  it should "handle multiple update operations" in {

    client.execute {
      bulk(
        update("2").in(indexname) doc("atomicweight" -> 6, "name" -> "carbon"),
        update("4").in(indexname) doc("atomicweight" -> 8, "name" -> "oxygen")
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    client.execute {
      get("2").from(indexname).storedFields("name")
    }.await.result.storedField("name").value shouldBe "carbon"

    client.execute {
      get("4").from(indexname).storedFields("name")
    }.await.result.storedField("name").value shouldBe "oxygen"
  }

  it should "handle createOnly in IndexRequest" in {

    client.execute {
      bulk(
        indexInto(indexname) fields("atomicweight" -> 6, "name" -> "carbon") id "10",
        indexInto(indexname) fields("atomicweight" -> 8, "name" -> "oxygen") id "11"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    client.execute {
      get("10").from(indexname).storedFields("name")
    }.await.result.storedField("name").value shouldBe "carbon"

    client.execute {
      get("11").from(indexname).storedFields("name")
    }.await.result.storedField("name").value shouldBe "oxygen"

    val result = client.execute {
      bulk(
        indexInto(indexname) fields("atomicweight" -> 6, "name" -> "carbon") id "10" createOnly false,
        indexInto(indexname) fields("atomicweight" -> 8, "name" -> "oxygen") id "11" createOnly true
      ).refresh(RefreshPolicy.Immediate)
    }.await.result

    result.errors shouldBe true
    result.failures.map(_.itemId).toSet shouldBe Set(1)
    result.successes.map(_.itemId).toSet shouldBe Set(0)
  }

  it should "handle multiple delete operations" in {

    client.execute {
      bulk(
        deleteById(indexname,"2"),
        deleteById(indexname,"4")
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    client.execute {
      get(indexname, "2")
    }.await.result.found shouldBe false

    client.execute {
      get(indexname, "4")
      get("4").from(indexname)
    }.await.result.found shouldBe false
  }

  it should "handle concurrency with internal versioning" in {

    val result = client.execute {
      bulk(
        indexInto(indexname).fields("atomicweight" -> 2, "name" -> "helium") versionType Internal id "2",
        indexInto(indexname).fields("atomicweight" -> 4, "name" -> "lithium") versionType Internal id "4"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result
    val wrongPrimaryTermResult = client.execute {
      bulk(
        result.items.map {
          responseItem => delete(responseItem.id).from(indexname)
            .ifPrimaryTerm(responseItem.primaryTerm + 1)
            .ifSeqNo(responseItem.seqNo)
            .versionType(Internal)
        }
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe true
    val wrongSeqNoResult = client.execute {
      bulk(
        result.items.map {
          responseItem => delete(responseItem.id).from(indexname)
            .ifPrimaryTerm(responseItem.primaryTerm)
            .ifSeqNo(responseItem.seqNo + 1)
            .versionType(Internal)
        }
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe true
    val successfulUpdateResult = client.execute {
      bulk(
        result.items.map {
          responseItem => delete(responseItem.id).from(indexname)
            .ifPrimaryTerm(responseItem.primaryTerm)
            .ifSeqNo(responseItem.seqNo)
            .versionType(Internal)
        }
      ).refresh(RefreshPolicy.Immediate)
    }.await
    successfulUpdateResult.isSuccess shouldBe true
  }

}
