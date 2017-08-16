package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DualClientTests
import org.scalatest.{FlatSpec, Matchers}
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._

import scala.util.Try

class DeleteTest extends FlatSpec with ElasticDsl with DualClientTests with Matchers {

  override protected def beforeRunTests(): Unit = {

    Try {
      execute {
        deleteIndex("places")
      }.await
    }

    execute(
      bulk(
        indexInto("places/cities") id 99 fields(
          "name" -> "London",
          "country" -> "UK"
        ),
        indexInto("places/cities") id 44 fields(
          "name" -> "Philadelphia",
          "country" -> "USA"
        ),
        indexInto("places/cities") id 615 fields(
          "name" -> "Middlesbrough",
          "country" -> "UK",
          "continent" -> "Europe"
        )
      ).immediateRefresh()
    ).await
  }

  "an index" should "do nothing when deleting a document where the id does not exist" in {

    execute {
      delete(141212) from "places" / "cities" refresh RefreshPolicy.Immediate
    }.await

    execute {
      search("places" / "cities").limit(0)
    }.await.totalHits shouldBe 3

  }

  it should "remove a document when deleting by id" in {
    execute {
      delete(99) from "places/cities" refresh RefreshPolicy.Immediate
    }.await

    execute {
      search("places" / "cities").limit(0)
    }.await.totalHits shouldBe 2
  }
}
