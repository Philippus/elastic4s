package com.sksamuel.elastic4s
package admin

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

  "an index" - {
    "can be snapshotted, fetched and restored" in {

      client.execute {
        create repository "_snapshot" `type` "fs" settings Map("location" -> "snapshottest")
      }.await(10.seconds)

      client.execute {
        create snapshot "snap" in "_snapshot" index "pizza" waitForCompletion true
      }.await(10.seconds)

      client.execute {
        get snapshot "snap" from "_snapshot"
      }.await(10.seconds)

      client.execute(bulk(
        index into "pizza/toppings" fields ("name" -> "spicy meatballs")
      )).await

      refresh("pizza")
      blockUntilCount(4, "pizza")

      client.execute {
        close index "pizza"
      }.await

      client.execute {
        restore snapshot "snap" from "_snapshot" index "pizza"
      }.await(10.seconds)

      // the doc added after the snapshot should be gone now
      refresh("pizza")
      blockUntilCount(3, "pizza")
    }
  }
}
