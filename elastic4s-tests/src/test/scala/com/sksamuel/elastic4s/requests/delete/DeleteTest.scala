package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class DeleteTest extends AnyFlatSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("places")
    }.await
  }

  client.execute(
    bulk(
      indexInto("places") id "99" fields (
        "name"      -> "London",
        "country"   -> "UK"
      ),
      indexInto("places") id "44" fields (
        "name"      -> "Philadelphia",
        "country"   -> "USA"
      ),
      indexInto("places") id "615" fields (
        "name"      -> "Middlesbrough",
        "country"   -> "UK",
        "continent" -> "Europe"
      )
    ).refreshImmediately
  ).await

  "a delete by id query" should "return success but with result = not_found when a document does not exist" in {

    client.execute {
      deleteById("places", "141212") refresh RefreshPolicy.Immediate
    }.await.result.result shouldBe "not_found"

    client.execute {
      search("places").limit(0)
    }.await.result.totalHits shouldBe 3
  }

  it should "return an error when the index does not exist" in {

    client.execute {
      deleteById("wooop", "141212") refresh RefreshPolicy.Immediate
    }.await.error.`type` shouldBe "index_not_found_exception"

    client.execute {
      search("places").limit(0)
    }.await.result.totalHits shouldBe 3
  }

  it should "remove a document when deleting by id" in {
    client.execute {
      deleteById("places", "99") refresh RefreshPolicy.Immediate
    }.await.result.result shouldBe "deleted"

    client.execute {
      search("places").limit(0)
    }.await.result.totalHits shouldBe 2
  }
}
