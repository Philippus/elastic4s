package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class DeleteTest extends FlatSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("places")
    }.await
  }

  client.execute(
    bulk(
      indexInto("places/cities") id "99" fields(
        "name" -> "London",
        "country" -> "UK"
      ),
      indexInto("places/cities") id "44" fields(
        "name" -> "Philadelphia",
        "country" -> "USA"
      ),
      indexInto("places/cities") id "615" fields(
        "name" -> "Middlesbrough",
        "country" -> "UK",
        "continent" -> "Europe"
      )
    ).refreshImmediately
  ).await

  "a delete by id query" should "return success but with result = not_found when a document does not exist" in {

    client.execute {
      delete("141212") from "places" / "cities" refresh RefreshPolicy.Immediate
    }.await.result.result shouldBe "not_found"

    client.execute {
      searchWithType("places" / "cities").limit(0)
    }.await.result.totalHits shouldBe 3
  }

  it should "return an error when the index does not exist" in {

    client.execute {
      delete("141212") from "wooop/la" refresh RefreshPolicy.Immediate
    }.await.error.`type` shouldBe "index_not_found_exception"

    client.execute {
      searchWithType("places" / "cities").limit(0)
    }.await.result.totalHits shouldBe 3
  }

  it should "remove a document when deleting by id" in {
    client.execute {
      delete("99") from "places/cities" refresh RefreshPolicy.Immediate
    }.await.result.result shouldBe "deleted"

    client.execute {
      searchWithType("places" / "cities").limit(0)
    }.await.result.totalHits shouldBe 2
  }
}
