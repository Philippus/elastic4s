package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import scala.concurrent.duration._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class UpdateTest extends FlatSpec with MockitoSugar with ElasticSugar {

  implicit val duration: Duration = 10.seconds

  client.execute {
    index into "scifi/trek" fields "character" -> "kirk" id 5
  }

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("scifi")
  blockUntilCount(1, "scifi")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "an update request" should "add a field when a script assigns a value" in {

    client.sync.execute {
      update id 5 in "scifi/trek" script "ctx._source.birthplace = 'iowa'"
    }
    refresh("scifi")

    var k = 0
    var hits = 0l
    while (k < 10 && hits == 0) {
      val resp = client result {
        search in "scifi" types "trek" term "birthplace" -> "iowa"
      }
      hits = resp.getHits.totalHits()
      Thread.sleep(k * 200)
      k = k + 1
    }
    assert(1 == hits)
  }
}
