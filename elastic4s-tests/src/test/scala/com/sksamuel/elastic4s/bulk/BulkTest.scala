package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._
import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, DualClientTests}
import org.scalatest.{FlatSpec, Matchers}

class BulkTest extends FlatSpec with Matchers with ElasticDsl with DualClientTests with ClassloaderLocalNodeProvider {

  override protected def beforeRunTests(): Unit = {
    execute {
      createIndex(indexname).mappings {
        mapping("elements").fields(
          intField("atomicweight").stored(true),
          textField("name").stored(true)
        )
      }
    }.await
  }

  "bulk request" should "handle multiple index operations" in {

    execute {
      bulk(
        indexInto(indexname / "elements") fields("atomicweight" -> 2, "name" -> "helium") id 2,
        indexInto(indexname / "elements") fields("atomicweight" -> 4, "name" -> "lithium") id 4
      ).refresh(RefreshPolicy.Immediate)
    }.await.errors shouldBe false

    execute {
      get(2).from(indexname / "elements")
    }.await.found shouldBe true

    execute {
      get(4).from(indexname / "elements")
    }.await.found shouldBe true
  }

  it should "return details of which items succeeded and failed" in {
    val result = execute {
      bulk(
        update(2).in(indexname / "elements").doc("atomicweight" -> 2, "name" -> "helium"),
        indexInto(indexname / "elements").fields("atomicweight" -> 8, "name" -> "oxygen") id 8,
        update(6).in(indexname / "elements").doc("atomicweight" -> 4, "name" -> "lithium"),
        delete(10).from(indexname / "elements")
      ).refresh(RefreshPolicy.Immediate)
    }.await

    result.hasFailures shouldBe true
    result.hasSuccesses shouldBe true
    result.errors shouldBe true

    result.failures.map(_.itemId).toSet shouldBe Set(2, 3)
    result.successes.map(_.itemId).toSet shouldBe Set(0, 1)
  }

  it should "handle multiple update operations" in {

    execute {
      bulk(
        update(2).in(indexname / "elements") doc("atomicweight" -> 6, "name" -> "carbon"),
        update(4).in(indexname / "elements") doc("atomicweight" -> 8, "name" -> "oxygen")
      ).refresh(RefreshPolicy.Immediate)
    }.await.errors shouldBe false

    execute {
      get(2).from(indexname / "elements").storedFields("name")
    }.await.storedField("name").value shouldBe "carbon"

    execute {
      get(4).from(indexname / "elements").storedFields("name")
    }.await.storedField("name").value shouldBe "oxygen"
  }

  it should "handle multiple delete operations" in {

    execute {
      bulk(
        delete(2).from(indexname / "elements"),
        delete(4).from(indexname / "elements")
      ).refresh(RefreshPolicy.Immediate)
    }.await.errors shouldBe false

    execute {
      get(2).from(indexname / "elements")
    }.await.found shouldBe false

    execute {
      get(4).from(indexname / "elements")
    }.await.found shouldBe false
  }
}
