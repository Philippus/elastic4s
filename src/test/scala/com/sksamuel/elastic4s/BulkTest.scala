package com.sksamuel.elastic4s

import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import ElasticDsl._
import scala.concurrent.duration._
import org.elasticsearch.common.Priority

/** @author Stephen Samuel */
class BulkTest extends FlatSpec with MockitoSugar with ElasticSugar {

  implicit val duration: Duration = 10.seconds

  client.sync.execute {
    index into "transport/air" fields "company" -> "delta" id 99
  }

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("transport")
  blockUntilCount(1, "transport")

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  "a bulk request" should "execute all index queries" in {

    client bulk (
      index into "transport/air" id 1 fields "company" -> "ba",
      index into "transport/air" id 2 fields "company" -> "aeroflot",
      index into "transport/air" id 3 fields "company" -> "american air",
      index into "transport/air" id 4 fields "company" -> "egypt air"
    )
    refresh("transport")
    blockUntilCount(5, "transport", "air")
  }

  "a bulk request" should "execute all delete queries" in {

    client bulk (
      delete(4) from "transport/air",
      delete id 2 from "transport/air"
    )
    refresh("transport")
    blockUntilCount(3, "transport", "air")
  }
}
