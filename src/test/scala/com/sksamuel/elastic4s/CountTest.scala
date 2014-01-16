package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.common.Priority
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.action.count.CountRequestBuilder

/** @author Stephen Samuel */
class CountTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.sync.execute {
    index into "london/landmarks" fields "name" -> "hampton court palace"
  }
  client.sync.execute {
    index into "london/landmarks" fields "name" -> "tower of london"
  }

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("london")
  blockUntilCount(2, "london")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "a count request" should "return total count when no query is specified" in {
    val resp = client.sync.execute {
      count from "london"
    }
    assert(2 === resp.getCount)
  }

  "a count request" should "return the document count based on the specified query" in {
    val resp = client.sync.execute {
      count from "london" query "tower"
    }
    assert(1 === resp.getCount)
  }
}
