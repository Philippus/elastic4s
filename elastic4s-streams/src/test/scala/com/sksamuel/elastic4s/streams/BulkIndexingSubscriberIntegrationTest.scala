package com.sksamuel.elastic4s.streams

import java.util.concurrent.{CountDownLatch, TimeUnit}

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.{BulkCompatibleDefinition, BulkItemResult}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.mappings.DynamicMapping.Strict
import com.sksamuel.elastic4s.mappings.FieldType.{IntegerType, StringType}
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class BulkIndexingSubscriberIntegrationTest extends WordSpec with ElasticSugar with Matchers {

  import ReactiveElastic._
  import scala.concurrent.duration._

  implicit val system = ActorSystem()

  val indexName = "bulkindexsubint"
  ensureIndexExists(indexName)

  "elastic-streams" should {
    "index all received data" in {
      implicit val builder = new ShipRequestBuilder(indexName)
      val completionLatch = new CountDownLatch(1)
      val subscriber = client.subscriber[Ship](10, 2, completionFn = () => completionLatch.countDown)
      ShipPublisher.subscribe(subscriber)
      completionLatch.await(5, TimeUnit.SECONDS)

      blockUntilCount(Ship.ships.length, indexName)
    }

    "index all received data even if the subscriber never completes" in {
      implicit val builder = new ShipRequestBuilder(indexName)


      // The short interval is just for the sake of test execution time, it's not a recommendation
      val subscriber = client.subscriber[Ship](8, 2, flushInterval = Some(500.millis))
      ShipEndlessPublisher.subscribe(subscriber)

      blockUntilCount(Ship.ships.length, indexName)
    }

    "index all receveid data and ignore failures" in {
      val strictIndex = "bulkindexfail"
      client.execute {
        createIndex(strictIndex) mappings ("ships" fields (
            "name" typed StringType,
            "description" typed IntegerType,
            "size" typed IntegerType
          ) dynamic Strict
        )
      }.await

      val errorsExpected = 2

      implicit val builder = new ShipRequestBuilder(strictIndex)
      val completionLatch = new CountDownLatch(1)
      val ackLatch = new CountDownLatch(Ship.ships.length - errorsExpected)
      val errorLatch = new CountDownLatch(errorsExpected)
      val subscriber = client.subscriber[Ship](10, 2, listener = new ResponseListener {
        override def onAck(resp: BulkItemResult): Unit = ackLatch.countDown()
        override def onFailure(resp: BulkItemResult): Unit = errorLatch.countDown()
      }, completionFn = () => completionLatch.countDown(), maxAttempts = 2, failureWait = 100.millis)
      ShipPublisher.subscribe(subscriber)
      completionLatch.await(5, TimeUnit.SECONDS)

      ackLatch.getCount should be (0)
      errorLatch.getCount should be (0)

      blockUntilCount(Ship.ships.length - errorsExpected, strictIndex)
    }
  }
}


object Ship {

  val ships = Array(
    Ship("clipper"),
    Ship("anaconda"),
    Ship("courier", Some("Fast ship that delivers")),
    Ship("python"),
    Ship("fer-de-lance"),
    Ship("sidewinder"),
    Ship("cobra"),
    Ship("viper"),
    Ship("eagle"),
    Ship("vulture"),
    Ship("dropship", Some("Drop it while its hot")),
    Ship("orca"),
    Ship("type6"),
    Ship("type7"),
    Ship("type9"),
    Ship("hauler"),
    Ship("adder"),
    Ship("asp explorer"),
    Ship("diamondback")
  )

}


class ShipRequestBuilder(indexName: String = "bulkindexsubint") extends RequestBuilder[Ship] {
  import ElasticJackson.Implicits._
  override def request(ship: Ship): BulkCompatibleDefinition = {
    index into s"$indexName/ships" source ship
  }
}


object ShipPublisher extends Publisher[Ship] {

  override def subscribe(s: Subscriber[_ >: Ship]): Unit = {
    var remaining = Ship.ships
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


object ShipEndlessPublisher extends Publisher[Ship] {

  override def subscribe(s: Subscriber[_ >: Ship]): Unit = {
    var remaining = Ship.ships
    s.onSubscribe(new Subscription {
      override def cancel(): Unit = ()
      override def request(n: Long): Unit = {
        remaining.take(n.toInt).foreach(s.onNext)
        remaining = remaining.drop(n.toInt)
      }
    })
  }
}


case class Ship(name: String, description: Option[String] = None, size: Int = Random.nextInt(100))
