package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class MultiGetDslTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    index into "coldplay/albums" fields ("name" -> "mylo xyloto") id 5
  }
  client.sync.execute {
    index into "coldplay/albums" fields ("name" -> "x&y") id 3
  }

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("coldplay")
  blockUntilCount(2, "coldplay")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "a multiget request" should "retrieve documents by id" in {

    val resp = client.sync.get(
      3 from "coldplay/albums",
      5 from "coldplay/albums",
      34 from "coldplay/albums"
    )
    assert(3 === resp.getResponses.size)
    assert("3" === resp.getResponses.toSeq(0).getResponse.getId)
    assert("5" === resp.getResponses.toSeq(1).getResponse.getId)
    assert(!resp.getResponses.toSeq(2).getResponse.isExists)
  }
}
