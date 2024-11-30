package com.sksamuel.elastic4s.requests.bulk

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.common.VersionType.Internal
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class BulkTest extends AnyFlatSpec with Matchers with DockerTests {

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
        indexInto(indexname) fields ("atomicweight" -> 2, "name" -> "helium") id "2",
        indexInto(indexname) fields ("atomicweight" -> 4, "name" -> "lithium") id "4"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    client.execute {
      get(Index(indexname), "2")
    }.await.result.found shouldBe true

    client.execute {
      get(Index(indexname), "4")
    }.await.result.found shouldBe true
  }

  it should "return details of which items succeeded and failed" in {
    val result = client.execute {
      bulk(
        updateById(Index(indexname), "2").doc("atomicweight" -> 2, "name" -> "helium"),
        indexInto(indexname).fields("atomicweight" -> 8, "name" -> "oxygen") id "8",
        updateById(Index(indexname), "6").doc("atomicweight" -> 4, "name" -> "lithium"),
        deleteById(Index(indexname), "10")
      ).refresh(RefreshPolicy.Immediate)
    }.await.result

    result.hasFailures shouldBe true
    result.hasSuccesses shouldBe true
    result.errors shouldBe true

    result.failures.map(_.itemId).toSet shouldBe Set(2, 3)
    result.successes.map(_.itemId).toSet shouldBe Set(0, 1)
  }

  it should "handle multiple update operations" in {
    val result = client.execute {
      bulk(
        updateById(Index(indexname), "2") doc ("atomicweight" -> 6, "name" -> "carbon"),
        updateById(Index(indexname), "4") doc ("atomicweight" -> 8, "name" -> "oxygen") fetchSource (true)
      ).refresh(RefreshPolicy.Immediate)
    }.await.result

    result.errors shouldBe false
    result.items.head.asInstanceOf[UpdateBulkResponseItem].source shouldBe None
    result.items.last.asInstanceOf[UpdateBulkResponseItem].source shouldBe Some {
      Map("atomicweight" -> 8, "name" -> "oxygen")
    }
    result.items.last.asInstanceOf[UpdateBulkResponseItem].sourceAsString shouldBe
      Some("{\"atomicweight\":8,\"name\":\"oxygen\"}")

    client.execute {
      get(Index(indexname), "2").storedFields("name")
    }.await.result.storedField("name").value shouldBe "carbon"

    client.execute {
      get(Index(indexname), "4").storedFields("name")
    }.await.result.storedField("name").value shouldBe "oxygen"
  }

  it should "handle createOnly in IndexRequest" in {

    client.execute {
      bulk(
        indexInto(indexname) fields ("atomicweight" -> 6, "name" -> "carbon") id "10",
        indexInto(indexname) fields ("atomicweight" -> 8, "name" -> "oxygen") id "11"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    client.execute {
      get(Index(indexname), "10").storedFields("name")
    }.await.result.storedField("name").value shouldBe "carbon"

    client.execute {
      get(Index(indexname), "11").storedFields("name")
    }.await.result.storedField("name").value shouldBe "oxygen"

    val result = client.execute {
      bulk(
        indexInto(indexname) fields ("atomicweight" -> 6, "name" -> "carbon") id "10" createOnly false,
        indexInto(indexname) fields ("atomicweight" -> 8, "name" -> "oxygen") id "11" createOnly true
      ).refresh(RefreshPolicy.Immediate)
    }.await.result

    result.errors shouldBe true
    result.failures.map(_.itemId).toSet shouldBe Set(1)
    result.successes.map(_.itemId).toSet shouldBe Set(0)
  }

  it should "handle multiple delete operations" in {

    client.execute {
      bulk(
        deleteById(indexname, "2"),
        deleteById(indexname, "4")
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    client.execute {
      get(indexname, "2")
    }.await.result.found shouldBe false

    client.execute {
      get(indexname, "4")
      get(Index(indexname), "4")
    }.await.result.found shouldBe false
  }

  it should "handle concurrency with internal versioning" in {

    val result                 = client.execute {
      bulk(
        indexInto(indexname).fields("atomicweight" -> 2, "name" -> "helium") versionType Internal id "2",
        indexInto(indexname).fields("atomicweight" -> 4, "name" -> "lithium") versionType Internal id "4"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result
    val wrongPrimaryTermResult = client.execute {
      bulk(
        result.items.map {
          responseItem =>
            deleteById(Index(indexname), responseItem.id)
              .ifPrimaryTerm(responseItem.primaryTerm + 1)
              .ifSeqNo(responseItem.seqNo)
              .versionType(Internal)
        }
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe true
    val wrongSeqNoResult       = client.execute {
      bulk(
        result.items.map {
          responseItem =>
            deleteById(Index(indexname), responseItem.id)
              .ifPrimaryTerm(responseItem.primaryTerm)
              .ifSeqNo(responseItem.seqNo + 1)
              .versionType(Internal)
        }
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe true
    val successfulUpdateResult = client.execute {
      bulk(
        result.items.map {
          responseItem =>
            deleteById(Index(indexname), responseItem.id)
              .ifPrimaryTerm(responseItem.primaryTerm)
              .ifSeqNo(responseItem.seqNo)
              .versionType(Internal)
        }
      ).refresh(RefreshPolicy.Immediate)
    }.await
    successfulUpdateResult.isSuccess shouldBe true
  }

}
