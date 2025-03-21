package com.sksamuel.elastic4s.akka.reactivestreams

import java.util.concurrent.{CountDownLatch, TimeUnit}

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.requests.bulk.BulkResponseItem
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class SubscriberListenerTest extends AnyWordSpec with Matchers with DockerTests {

  import ReactiveElastic._

  implicit val system: ActorSystem         = ActorSystem()
  implicit val builder: ShipRequestBuilder = new ShipRequestBuilder()

  Try {
    client.execute {
      createIndex("subscriberlistenertest")
    }.await
  }

  "Reactive streams subscriber" should {
    "invoke listener for each confirmed doc" ignore {

      val latch = new CountDownLatch(Ship.ships.length)

      val config     = SubscriberConfig(listener = new ResponseListener[Ship] {
        def onAck(resp: BulkResponseItem, original: Ship): Unit = latch.countDown()
      })
      val subscriber = client.subscriber[Ship](config)
      ShipPublisher.subscribe(subscriber)

      latch.await(1, TimeUnit.MINUTES) shouldBe true
    }
  }
}
