package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class BulkTest extends FlatSpec with Matchers with DockerTests {

  private val indexname = "bulkytest"

  Try {
    http.execute {
      deleteIndex(indexname)
    }.await
  }

  http.execute {
    createIndex(indexname).mappings {
      mapping("elements").fields(
        intField("atomicweight").stored(true),
        textField("name").stored(true)
      )
    }
  }.await

  "bulk request" should "handle multiple index operations" in {

    http.execute {
      bulk(
        indexInto(indexname / "elements") fields("atomicweight" -> 2, "name" -> "helium") id "2",
        indexInto(indexname / "elements") fields("atomicweight" -> 4, "name" -> "lithium") id "4"
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.errors shouldBe false

    http.execute {
      get("2").from(indexname)
    }.await.right.get.result.found shouldBe true

    http.execute {
      get("4").from(indexname)
    }.await.right.get.result.found shouldBe true
  }

  it should "return details of which items succeeded and failed" in {
    val result = http.execute {
      bulk(
        update("2").in(indexname / "elements").doc("atomicweight" -> 2, "name" -> "helium"),
        indexInto(indexname / "elements").fields("atomicweight" -> 8, "name" -> "oxygen") id "8",
        update("6").in(indexname / "elements").doc("atomicweight" -> 4, "name" -> "lithium"),
        delete("10").from(indexname / "elements")
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result

    result.hasFailures shouldBe true
    result.hasSuccesses shouldBe true
    result.errors shouldBe true

    result.failures.map(_.itemId).toSet shouldBe Set(2, 3)
    result.successes.map(_.itemId).toSet shouldBe Set(0, 1)
  }

  it should "handle multiple update operations" in {

    http.execute {
      bulk(
        update("2").in(indexname / "elements") doc("atomicweight" -> 6, "name" -> "carbon"),
        update("4").in(indexname / "elements") doc("atomicweight" -> 8, "name" -> "oxygen")
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.errors shouldBe false

    http.execute {
      get("2").from(indexname).storedFields("name")
    }.await.right.get.result.storedField("name").value shouldBe "carbon"

    http.execute {
      get("4").from(indexname).storedFields("name")
    }.await.right.get.result.storedField("name").value shouldBe "oxygen"
  }

  it should "handle createOnly in IndexRequest" in {

    http.execute {
      bulk(
        indexInto(indexname / "elements") fields("atomicweight" -> 6, "name" -> "carbon") id "10",
        indexInto(indexname / "elements") fields("atomicweight" -> 8, "name" -> "oxygen") id "11"
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.errors shouldBe false

    http.execute {
      get("10").from(indexname).storedFields("name")
    }.await.right.get.result.storedField("name").value shouldBe "carbon"

    http.execute {
      get("11").from(indexname).storedFields("name")
    }.await.right.get.result.storedField("name").value shouldBe "oxygen"

    val result = http.execute {
      bulk(
        indexInto(indexname / "elements") fields("atomicweight" -> 6, "name" -> "carbon") id "10" createOnly false,
        indexInto(indexname / "elements") fields("atomicweight" -> 8, "name" -> "oxygen") id "11" createOnly true
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result

    result.errors shouldBe true
    result.failures.map(_.itemId).toSet shouldBe Set(1)
    result.successes.map(_.itemId).toSet shouldBe Set(0)
  }

  it should "handle multiple delete operations" in {

    http.execute {
      bulk(
        deleteById(indexname, "elements", "2"),
        deleteById(indexname, "elements", "4")
      ).refresh(RefreshPolicy.Immediate)
    }.await.right.get.result.errors shouldBe false

    http.execute {
      get(indexname, "elements", "2")
    }.await.right.get.result.found shouldBe false

    http.execute {
      get(indexname, "elements", "4")
      get("4").from(indexname)
    }.await.right.get.result.found shouldBe false
  }
}
