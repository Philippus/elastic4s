package com.sksamuel.elastic4s
package admin

import org.apache.commons.io.FileUtils
import org.scalatest.FreeSpec
import org.scalatest.mock.MockitoSugar

import scala.concurrent.duration._

class SnapshotTest extends FreeSpec with MockitoSugar with ElasticSugar with ElasticDsl {

  client.execute(bulk(
    index into "pizza/toppings" fields ("name" -> "chicken"),
    index into "pizza/toppings" fields ("name" -> "pepperoni"),
    index into "pizza/toppings" fields ("name" -> "onions")
  )).await

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
        snapshot delete "snap" in "_snapshot"
      }.await(10.seconds)

      client.execute {
        snapshot create "snap" in "_snapshot" index "pizza" waitForCompletion true
      }.await(10.seconds)

      client.execute(bulk(
        index into "pizza/toppings" fields ("name" -> "spicy meatballs")
      )).await

      refresh("pizza")
      blockUntilCount(4, "pizza")

      client.close("pizza").await

      client.execute {
        snapshot restore "snap" from "_snapshot"
      }.await(10.seconds)

      // the doc added after the snapshot should be gone now
      refresh("pizza")
      blockUntilCount(3, "pizza")
    }
  }
}
