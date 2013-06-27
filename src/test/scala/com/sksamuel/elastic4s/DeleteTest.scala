package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.IndexDsl._
import DeleteDsl._

/** @author Stephen Samuel */
class DeleteTest extends FlatSpec with MockitoSugar with ElasticSugar {

    client.execute {
        index into "places/cities" fields (
          "name" -> "London",
          "country" -> "UK"
          ) id 99
    }
    client.execute {
        index into "places/cities" fields (
          "name" -> "Philadelphia",
          "country" -> "USA"
          ) id 44
    }
    refresh("places")
    blockUntilCount(2, "places")

    "a search index" should "do nothing when deleting a document where the id does not exist" in {
        client.execute {
            delete id 100 from "places/cities"
        }
        refresh("places")
        Thread.sleep(1000)
        blockUntilCount(2, "places")
    }

    "a search index" should "do nothing when deleting a document where the query returns no results" in {
        client.execute {
            delete from "places/cities" where "paris"
        }
        refresh("places")
        Thread.sleep(1000)
        blockUntilCount(2, "places")
    }

    "a search index" should "remove a document when deleting by id" in {
        client.execute {
            delete id 99 from "places/cities"
        }
        refresh("places")
        blockUntilCount(1, "places")
    }

    "a search index" should "remove a document when deleting by query" in {
        client.execute {
            delete from "places/cities" where "country:USA"
        }
        refresh("places")
        blockUntilCount(0, "places")
    }
}
