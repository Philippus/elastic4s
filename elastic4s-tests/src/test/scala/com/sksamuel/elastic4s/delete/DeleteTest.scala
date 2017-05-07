package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.FlatSpec
import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._

class DeleteTest extends FlatSpec with ElasticDsl with DualElasticSugar with DualClient {

  override protected def beforeRunTests(): Unit = {
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
      )
    ).await

    refresh("places")
    blockUntilCount(3, "places")
  }

  "an index" should "do nothing when deleting a document where the id does not exist using id" in {
    execute {
      delete(141212) from "places" -> "cities"
    }.await
    refresh("places")
    Thread.sleep(1000)
    blockUntilCount(3, "places")
  }

  it should "remove a document when deleting by id" in {
    execute {
      delete(99) from "places/cities"
    }.await
    refresh("places")
    blockUntilCount(2, "places")
  }
}
