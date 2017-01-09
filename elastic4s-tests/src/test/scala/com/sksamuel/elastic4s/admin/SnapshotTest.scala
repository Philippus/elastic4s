package com.sksamuel.elastic4s
package admin

import org.scalatest.FreeSpec
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.mockito.MockitoSugar

import scala.concurrent.duration._

class SnapshotTest extends FreeSpec with MockitoSugar with ElasticSugar with ElasticDsl {

  client.execute(bulk(
    indexInto("pizza/toppings") fields ("name" -> "chicken"),
    indexInto("pizza/toppings") fields ("name" -> "pepperoni"),
    indexInto("pizza/toppings") fields ("name" -> "onions")
  )).await

  refresh("pizza")
  blockUntilCount(3, "pizza")

  "an index" - {
    "can be snapshotted, fetched and restored" in {

      client.execute {
        createRepository("_snapshot") `type` "fs" settings Map("location" -> node.pathRepo.toAbsolutePath.toString)
      }.await(10.seconds)

      client.execute {
        createSnapshot("snap") in "_snapshot" index "pizza" waitForCompletion true
      }.await(10.seconds)

      client.execute {
        getSnapshot("snap") from "_snapshot"
      }.await(10.seconds)

      client.execute(bulk(
        indexInto("pizza/toppings") fields ("name" -> "spicy meatballs")
      )).await

      refresh("pizza")
      blockUntilCount(4, "pizza")

      client.execute {
        closeIndex("pizza")
      }.await

      client.execute {
        restoreSnapshot("snap") from "_snapshot" index "pizza"
      }.await(10.seconds)

      // the doc added after the snapshot should be gone now
      refresh("pizza")
      blockUntilCount(3, "pizza")
    }
  }
}
