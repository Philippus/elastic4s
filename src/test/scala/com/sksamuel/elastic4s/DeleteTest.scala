package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class DeleteTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    index into "places/cities" fields(
      "name" -> "London",
      "country" -> "UK"
      ) id 99
  }
  client.execute {
    index into "places/cities" fields(
      "name" -> "Philadelphia",
      "country" -> "USA"
      ) id 44
  }

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
    client.sync.delete {
      "places/cities" -> 99
    }
    refresh("places")
    blockUntilCount(1, "places")
  }

  "a search index" should "remove a document when deleting by query" in {
    client.sync.delete {
      "places" types "cities" where "country:USA"
    }
    refresh("places")
    blockUntilCount(0, "places")
  }
}
