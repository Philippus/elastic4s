package com.sksamuel.elastic4s.streams

import java.util.concurrent.{CountDownLatch, TimeUnit}

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.testkit.ElasticSugar
import com.sksamuel.elastic4s.{BulkCompatibleDefinition, ElasticDsl}
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import org.scalatest.{Matchers, WordSpec}

class BulkIndexingSubscriberIntegrationTest extends WordSpec with ElasticSugar with Matchers {

  import ElasticDsl._
  import ElasticJackson.Implicits._
  import ReactiveElastic._
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val system = ActorSystem()

  implicit object ShipRequestBuilder extends RequestBuilder[Ship] {
    override def request(ship: Ship): BulkCompatibleDefinition = {
      index into "bulkindexsubint" / "ships" source ship
    }
  }

  ensureIndexExists("bulkindexsubint")

  "elastic-streams" should {
    "index all received data" in {

      val completionLatch = new CountDownLatch(1)
      val subscriber = client.subscriber[Ship](10, 2, completionFn = () => completionLatch.countDown)
      ShipPublisher.subscribe(subscriber)
      completionLatch.await(5, TimeUnit.SECONDS)

      blockUntilCount(ShipPublisher.ships.length, "bulkindexsubint")
    }
  }
}

object ShipPublisher extends Publisher[Ship] {

  val ships = Array(
    Ship("clipper"),
    Ship("anaconda"),
    Ship("courier"),
    Ship("python"),
    Ship("fer-de-lance"),
    Ship("sidewinder"),
    Ship("cobra"),
    Ship("viper"),
    Ship("eagle"),
    Ship("vulture"),
    Ship("dropship"),
    Ship("orca"),
    Ship("type6"),
    Ship("type7"),
    Ship("type9"),
    Ship("hauler"),
    Ship("adder"),
    Ship("asp explorer"),
    Ship("diamondback")
  )

  override def subscribe(s: Subscriber[_ >: Ship]): Unit = {
    var remaining = ships
    s.onSubscribe(new Subscription {
      override def cancel(): Unit = ()
      override def request(n: Long): Unit = {
        remaining.take(n.toInt).foreach(s.onNext)
        remaining = remaining.drop(n.toInt)
        if (remaining.isEmpty)
          s.onComplete()
      }
    })
  }
}

case class Ship(name: String)