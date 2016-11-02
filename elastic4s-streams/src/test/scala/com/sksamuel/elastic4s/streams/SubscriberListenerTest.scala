package com.sksamuel.elastic4s.streams

import java.util.concurrent.{TimeUnit, CountDownLatch}

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.BulkItemResult
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class SubscriberListenerTest extends WordSpec with Matchers with ElasticSugar {

  import ReactiveElastic._

  implicit val system = ActorSystem()
  implicit val builder = new ShipRequestBuilder()

  ensureIndexExists("subscriberlistenertest")

  "Reactive streams subscriber" should {
    "invoke listener for each confirmed doc" in {

      val latch = new CountDownLatch(Ship.ships.length)

      val config = SubscriberConfig(listener = new ResponseListener {
        override def onAck(resp: BulkItemResult): Unit = latch.countDown()
      })
      val subscriber = client.subscriber[Ship](config)
      ShipPublisher.subscribe(subscriber)

      latch.await(1, TimeUnit.MINUTES) shouldBe true
    }
  }
}
