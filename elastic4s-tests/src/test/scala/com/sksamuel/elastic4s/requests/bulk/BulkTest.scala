package com.sksamuel.elastic4s.requests.bulk

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
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
    createIndex(indexname).mappings {
      mapping("elements").fields(
        intField("atomicweight").stored(true),
        textField("name").stored(true)
      )
    }
  }.await

  "bulk request" should "handle multiple index operations" in {

    client.execute {
      bulk(
        indexInto(indexname / "elements") fields("atomicweight" -> 2, "name" -> "helium") id "2",
        indexInto(indexname / "elements") fields("atomicweight" -> 4, "name" -> "lithium") id "4"
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
        update("2").in(indexname / "elements").doc("atomicweight" -> 2, "name" -> "helium"),
        indexInto(indexname / "elements").fields("atomicweight" -> 8, "name" -> "oxygen") id "8",
        update("6").in(indexname / "elements").doc("atomicweight" -> 4, "name" -> "lithium"),
        delete("10").from(indexname / "elements")
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
        update("2").in(indexname / "elements") doc("atomicweight" -> 6, "name" -> "carbon"),
        update("4").in(indexname / "elements") doc("atomicweight" -> 8, "name" -> "oxygen")
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
        indexInto(indexname / "elements") fields("atomicweight" -> 6, "name" -> "carbon") id "10",
        indexInto(indexname / "elements") fields("atomicweight" -> 8, "name" -> "oxygen") id "11"
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
        indexInto(indexname / "elements") fields("atomicweight" -> 6, "name" -> "carbon") id "10" createOnly false,
        indexInto(indexname / "elements") fields("atomicweight" -> 8, "name" -> "oxygen") id "11" createOnly true
      ).refresh(RefreshPolicy.Immediate)
    }.await.result

    result.errors shouldBe true
    result.failures.map(_.itemId).toSet shouldBe Set(1)
    result.successes.map(_.itemId).toSet shouldBe Set(0)
  }

  it should "handle multiple delete operations" in {

    client.execute {
      bulk(
        deleteById(indexname, "elements", "2"),
        deleteById(indexname, "elements", "4")
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    client.execute {
      get(indexname, "elements", "2")
    }.await.result.found shouldBe false

    client.execute {
      get(indexname, "elements", "4")
      get("4").from(indexname)
    }.await.result.found shouldBe false
  }

  it should "handle version in delete operation" in {

    client.execute {
      bulk(
        indexInto(indexname / "elements") fields("atomicweight" -> 6, "name" -> "carbon") id "20",
        indexInto(indexname / "elements") fields("atomicweight" -> 8, "name" -> "oxygen") id "21"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    val result = client.execute {
      bulk(
        deleteById(indexname, "elements", "20") version(1),
        deleteById(indexname, "elements", "21") version(2)
      ).refresh(RefreshPolicy.Immediate)
    }.await.result

    result.errors shouldBe true
    result.failures.map(_.itemId).toSet shouldBe Set(1)
    result.successes.map(_.itemId).toSet shouldBe Set(0)

    client.execute {
      get(indexname, "elements", "20")
    }.await.result.found shouldBe false

    client.execute {
      get(indexname, "elements", "21")
    }.await.result.found shouldBe true

  }

  it should "handle version in updateById operation" in {

    client.execute {
      bulk(
        indexInto(indexname / "elements") fields("atomicweight" -> 6, "name" -> "carbon") id "22",
        indexInto(indexname / "elements") fields("atomicweight" -> 8, "name" -> "oxygen") id "23"
      ).refresh(RefreshPolicy.Immediate)
    }.await.result.errors shouldBe false

    val result = client.execute {
      bulk(
        updateById(indexname, "elements", "22") doc("atomicweight" -> 9) version(1),
        updateById(indexname, "elements", "23") doc("atomicweight" -> 10) version(2)
      ).refresh(RefreshPolicy.Immediate)
    }.await.result

    result.errors shouldBe true
    result.failures.map(_.itemId).toSet shouldBe Set(1)
    result.successes.map(_.itemId).toSet shouldBe Set(0)

    val carbon = client.execute {
      get(indexname, "elements", "22")
    }.await.result
    carbon.found shouldBe true
    carbon.sourceAsMap("atomicweight") shouldBe 9

    val oxygen = client.execute {
      get(indexname, "elements", "23")
    }.await.result
    oxygen.found shouldBe true
    oxygen.sourceAsMap("atomicweight") shouldBe 8

  }

}
