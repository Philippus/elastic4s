package com.sksamuel.elastic4s
package admin

import org.apache.commons.io.FileUtils
import org.elasticsearch.common.Priority
import org.scalatest.FreeSpec
import org.scalatest.mock.MockitoSugar

import scala.concurrent.duration._

class SnapshotTest extends FreeSpec with MockitoSugar with ElasticSugar with ElasticDsl {

  client.sync.execute(bulk(
    index into "pizza/toppings" fields ("name" -> "chicken"),
    index into "pizza/toppings" fields ("name" -> "pepperoni"),
    index into "pizza/toppings" fields ("name" -> "onions")
  ))

  client.admin.cluster.prepareHealth().setWaitForEvents(Priority.LANGUID).setWaitForGreenStatus().execute().actionGet

  refresh("pizza")
  blockUntilCount(3, "pizza")

  val location = FileUtils.getTempDirectoryPath

  "an index" - {
    "can be snapshotted and restored" in {

      client.execute {
        repository create "_snapshot" `type` "fs" settings Map("location" -> location)
      }.await(10.seconds)

      // clean up from previous tests
      client.execute {
        snapshot delete "snap1" in "_snapshot"
      }.await(10.seconds)

      client.execute {
        snapshot create "snap1" in "_snapshot" waitForCompletion true
      }.await(10.seconds)

      client.sync.execute(bulk(
        index into "pizza/toppings" fields ("name" -> "spicy meatballs")
      ))

      refresh("pizza")
      blockUntilCount(4, "pizza")

      client.close("pizza")

      client.execute {
        snapshot restore "snap1" from "_snapshot"
      }.await(10.seconds)

      // the doc added after the snapshot should be gone now
      refresh("pizza")
      blockUntilCount(3, "pizza")
    }
  }
}
