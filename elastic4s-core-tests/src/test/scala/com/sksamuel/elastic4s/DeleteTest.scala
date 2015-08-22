package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.testkit.ElasticSugar

/** @author Stephen Samuel */
class DeleteTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute(
    bulk(
      index into "places/cities" id 99 fields (
        "name" -> "London",
        "country" -> "UK"
      ),
      index into "places/cities" id 44 fields (
        "name" -> "Philadelphia",
        "country" -> "USA"
      ),
      index into "places/cities" id 615 fields (
        "name" -> "Middlesbrough",
        "country" -> "UK",
        "continent" -> "Europe"
      )
    )
  ).await

  refresh("places")
  blockUntilCount(3, "places")

  "an index" should "do nothing when deleting a document where the id does not exist using where" in {
    client.execute {
      delete from "places" -> "cities" where "name" -> "sammy"
    }.await
    refresh("places")
    Thread.sleep(1000)
    blockUntilCount(3, "places")
  }

  it should "do nothing when deleting a document where the id does not exist using id" in {
    client.execute {
      delete id 141212 from "places" -> "cities"
    }.await
    refresh("places")
    Thread.sleep(1000)
    blockUntilCount(3, "places")
  }

  it should "do nothing when deleting a document where the query returns no results" in {
    client.execute {
      delete from "places" types "cities" where "paris"
    }.await
    refresh("places")
    Thread.sleep(1000)
    blockUntilCount(3, "places")
  }

  it should "remove a document when deleting by id" in {
    client.execute {
      delete id 99 from "places/cities"
    }.await
    refresh("places")
    blockUntilCount(2, "places")
  }

  it should "remove a document when deleting by query" in {
    client.execute {
      delete from "places" types "cities" where matchQuery("continent", "Europe")
    }.await
    refresh("places")
    blockUntilCount(1, "places")
  }
}
