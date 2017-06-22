//package com.sksamuel.elastic4s.streams
//
//import java.util.concurrent.{CountDownLatch, TimeUnit}
//
//import akka.actor.ActorSystem
//import com.sksamuel.elastic4s.BulkCompatibleDefinition
//import com.sksamuel.elastic4s.ElasticDsl.index
//import com.sksamuel.elastic4s.jackson.ElasticJackson
//import com.sksamuel.elastic4s.testkit.ElasticSugar
//import org.reactivestreams.{Publisher, Subscriber, Subscription}
//import org.scalatest.{Matchers, WordSpec}
//
//class BulkIndexingSubscriberIntegrationTest extends WordSpec with ElasticSugar with Matchers {
//
//  import ReactiveElastic._
//
//  implicit val system = ActorSystem()
//  implicit val builder = ShipRequestBuilder
//
//  ensureIndexExists("bulkindexsubint")
//
//  "elastic-streams" should {
//    "index all received data" in {
//
//      val completionLatch = new CountDownLatch(1)
//      val subscriber = client.subscriber[Ship](10, 2, completionFn = () => completionLatch.countDown)
//      ShipPublisher.subscribe(subscriber)
//      completionLatch.await(5, TimeUnit.SECONDS)
//
//      blockUntilCount(Ship.ships.length, "bulkindexsubint")
//    }
//
//    "index all received data even if the subscriber never completes" in {
//
//      import scala.concurrent.duration._
//
//      // The short interval is just for the sake of test execution time, it's not a recommendation
//      val subscriber = client.subscriber[Ship](8, 2, flushInterval = Some(500.millis))
//      ShipEndlessPublisher.subscribe(subscriber)
//
//      blockUntilCount(Ship.ships.length, "bulkindexsubint")
//    }
//  }
//}
//
//
//object Ship {
//
//  val ships = Array(
//    Ship("clipper"),
//    Ship("anaconda"),
//    Ship("courier"),
//    Ship("python"),
//    Ship("fer-de-lance"),
//    Ship("sidewinder"),
//    Ship("cobra"),
//    Ship("viper"),
//    Ship("eagle"),
//    Ship("vulture"),
//    Ship("dropship"),
//    Ship("orca"),
//    Ship("type6"),
//    Ship("type7"),
//    Ship("type9"),
//    Ship("hauler"),
//    Ship("adder"),
//    Ship("asp explorer"),
//    Ship("diamondback")
//  )
//
//}
//
//
//object ShipRequestBuilder extends RequestBuilder[Ship] {
//  import ElasticJackson.Implicits._
//  override def request(ship: Ship): BulkCompatibleDefinition = {
//    index into "bulkindexsubint/ships" source ship
//  }
//}
//
//
//object ShipPublisher extends Publisher[Ship] {
//
//  override def subscribe(s: Subscriber[_ >: Ship]): Unit = {
//    var remaining = Ship.ships
//    s.onSubscribe(new Subscription {
//      override def cancel(): Unit = ()
//      override def request(n: Long): Unit = {
//        remaining.take(n.toInt).foreach(s.onNext)
//        remaining = remaining.drop(n.toInt)
//        if (remaining.isEmpty)
//          s.onComplete()
//      }
//    })
//  }
//}
//
//
//object ShipEndlessPublisher extends Publisher[Ship] {
//
//  override def subscribe(s: Subscriber[_ >: Ship]): Unit = {
//    var remaining = Ship.ships
//    s.onSubscribe(new Subscription {
//      override def cancel(): Unit = ()
//      override def request(n: Long): Unit = {
//        remaining.take(n.toInt).foreach(s.onNext)
//        remaining = remaining.drop(n.toInt)
//      }
//    })
//  }
//}
//
//
//case class Ship(name: String)
