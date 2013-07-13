package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class GetTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    index into "beer/lager" fields(
      "name" -> "coors light",
      "brand" -> "coors"
      ) id 4
  }
  client.execute {
    index into "beer/lager" fields(
      "name" -> "bud lite",
      "brand" -> "bud"
      ) id 8
  }

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("beer")
  blockUntilCount(2, "beer")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "a get request" should "retrieve a document by id" in {

    val resp = client.sync.execute {
      get id 8 from "beer/lager"
    }
    assert("8" === resp.getId)
  }
}
