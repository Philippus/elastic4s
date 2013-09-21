package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class DeleteTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.bulk(
    index into "places/cities" id 99 fields(
      "name" -> "London",
      "country" -> "UK"
      ),
    index into "places/cities" id 44 fields(
      "name" -> "Philadelphia",
      "country" -> "USA"
      )
  )

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("places")
  blockUntilCount(2, "places")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "a search index" should "do nothing when deleting a document where the id does not exist" in {
    client.delete {
      "places/cities" -> 3423424
    }
    refresh("places")
    Thread.sleep(1000)
    blockUntilCount(2, "places")
  }

  "a search index" should "do nothing when deleting a document where the query returns no results" in {
    client.execute {
      "places" types "cities" where "paris"
    }
    refresh("places")
    Thread.sleep(1000)
    blockUntilCount(2, "places")
  }

  "a search index" should "remove a document when deleting by id" in {
    client.sync.execute {
      delete id 99 from "places/cities"
    }
    refresh("places")
    blockUntilCount(1, "places")
  }

  "a search index" should "remove a document when deleting by query" in {
    client.sync.delete {
      "places" types Seq("cities", "countries") where "continent:Europe"
    }
    refresh("places")
    blockUntilCount(0, "places")
  }
}
