package com.sksamuel.elastic4s
package admin

import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, ElasticSugar}
import org.scalatest.FreeSpec
import org.scalatest.mockito.MockitoSugar

import scala.concurrent.duration._

class SnapshotTest extends FreeSpec with MockitoSugar with ClassloaderLocalNodeProvider with ElasticDsl with ElasticSugar {

  client.execute(
    bulk(
      indexInto("pizza/toppings") fields ("name" -> "chicken"),
      indexInto("pizza/toppings") fields ("name" -> "pepperoni"),
      indexInto("pizza/toppings") fields ("name" -> "onions")
    ).immediateRefresh()
  ).await

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

      blockUntilCount(4, "pizza")

      client.execute(
        indexInto("pizza/toppings") fields ("name" -> "spicy meatballs")
      ).await

      client.execute {
        closeIndex("pizza")
      }.await

      client.execute {
        restoreSnapshot("snap") from "_snapshot" index "pizza"
      }.await(10.seconds)

      blockUntilCount(3, "pizza")
    }
  }
}
