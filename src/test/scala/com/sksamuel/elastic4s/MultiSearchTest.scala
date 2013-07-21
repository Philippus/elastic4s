package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class MultiSearchTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    index into "jtull/albums" fields ("name" -> "aqualung") id 14
  }
  client.sync.execute {
    index into "jtull/albums" fields ("name" -> "passion play") id 51
  }

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("jtull")
  blockUntilCount(2, "jtull")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "a multi search request" should "find matching documents for all queries" in {

    val resp = client.sync.search(
      search in "jtull" query "aqualung",
      search in "jtull" query "passion"
    )
    assert(2 === resp.getResponses.size)
    assert("14" === resp.getResponses.toSeq(0).getResponse.getHits.getAt(0).id())
    assert("51" === resp.getResponses.toSeq(1).getResponse.getHits.getAt(0).id())
  }
}
