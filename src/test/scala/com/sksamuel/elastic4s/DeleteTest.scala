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

  "an index" should "do nothing when deleting a document where the id does not exist using where" in {
    client.execute {
      delete from "places" -> "cities" where "name" -> "sammy"
    }
    refresh("places")
    Thread.sleep(1000)
    blockUntilCount(2, "places")
  }

  it should "do nothing when deleting a document where the id does not exist using id" in {
    client.execute {
      delete id 141212 from "places" -> "cities"
    }
    refresh("places")
    Thread.sleep(1000)
    blockUntilCount(2, "places")
  }

  it should "do nothing when deleting a document where the query returns no results" in {
    client.execute {
      delete from "places" types "cities" where "paris"
    }
    refresh("places")
    Thread.sleep(1000)
    blockUntilCount(2, "places")
  }

  it should "remove a document when deleting by id" in {
    client.sync.execute {
      delete id 99 from "places/cities"
    }
    refresh("places")
    blockUntilCount(1, "places")
  }

  it should "remove a document when deleting by query" in {
    client.sync.execute {
      delete from Seq("places") types Seq("cities") where "continent:Europe"
    }
    refresh("places")
    blockUntilCount(0, "places")
  }
}
