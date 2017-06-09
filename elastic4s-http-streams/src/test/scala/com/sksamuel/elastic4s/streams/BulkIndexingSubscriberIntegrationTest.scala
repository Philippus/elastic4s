//package com.sksamuel.elastic4s.streams
//
//import java.util.concurrent.{CountDownLatch, TimeUnit}
//
//import akka.actor.ActorSystem
//import com.sksamuel.elastic4s.ElasticsearchClientUri
//import com.sksamuel.elastic4s.bulk.BulkCompatibleDefinition
//import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
//import com.sksamuel.elastic4s.http.bulk.BulkResponseItem
//import com.sksamuel.elastic4s.mappings.dynamictemplate.DynamicMapping.Strict
//import com.sksamuel.elastic4s.testkit.SharedElasticSugar
//import org.reactivestreams.{Publisher, Subscriber, Subscription}
//import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
//
//import scala.util.Random
//
//class BulkIndexingSubscriberIntegrationTest extends WordSpec with SharedElasticSugar with Matchers with BeforeAndAfter with ElasticDsl {
//
//  import ReactiveElastic._
//  import scala.concurrent.duration._
//
//  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))
//
//  implicit val system = ActorSystem()
//
//  val indexName = "bulkindexsubint"
//  val strictIndex = "bulkindexfail"
//
//  after {
//    deleteIndex(indexName)
//    deleteIndex(strictIndex)
//  }
//
//  "elastic-streams" should {
//    "index all received data" in {
//      ensureIndexExists(indexName)
//      implicit val builder = new ShipRequestBuilder(indexName)
//
//      val completionLatch = new CountDownLatch(1)
//      val subscriber = http.subscriber[Ship](10, 2, completionFn = () => completionLatch.countDown())
//      ShipPublisher.subscribe(subscriber)
//      completionLatch.await(5, TimeUnit.SECONDS)
//
//      blockUntilCount(Ship.ships.length, indexName)
//    }
//
//    "index all received data even if the subscriber never completes" in {
//      ensureIndexExists(indexName)
//      implicit val builder = new ShipRequestBuilder(indexName)
//
//      // The short interval is just for the sake of test execution time, it's not a recommendation
//      val subscriber = http.subscriber[Ship](8, 2, flushInterval = Some(500.millis))
//      ShipEndlessPublisher.subscribe(subscriber)
//
//      blockUntilCount(Ship.ships.length, indexName)
//    }
//
//    "index all received data and ignore failures" in {
//
//      http.execute {
//        createIndex(strictIndex).mappings(
//          mapping("ships").fields(
//            textField("name"),
//            intField("description"),
//            intField("size")
//          ) dynamic Strict
//        )
//      }.await
//      implicit val builder = new ShipRequestBuilder(strictIndex)
//
//      val errorsExpected = 2
//
//      val completionLatch = new CountDownLatch(1)
//      val ackLatch = new CountDownLatch(Ship.ships.length - errorsExpected)
//      val errorLatch = new CountDownLatch(errorsExpected)
//      val subscriber = http.subscriber[Ship](10, 2, listener = new ResponseListener[Ship] {
//        override def onAck(resp: BulkResponseItem, ship: Ship): Unit = ackLatch.countDown()
//        override def onFailure(resp: BulkResponseItem, ship: Ship): Unit = errorLatch.countDown()
//      }, completionFn = () => completionLatch.countDown(), maxAttempts = 2, failureWait = 100.millis)
//      ShipPublisher.subscribe(subscriber)
//      completionLatch.await(5, TimeUnit.SECONDS)
//
//      ackLatch.getCount should be(0)
//      errorLatch.getCount should be(0)
//
//      blockUntilCount(Ship.ships.length - errorsExpected, strictIndex)
//    }
//  }
//}
//
//object Ship {
//
//  val ships = List(
//    Ship("clipper"),
//    Ship("anaconda"),
//    Ship("courier", Some("Fast ship that delivers")),
//    Ship("python"),
//    Ship("fer-de-lance"),
//    Ship("sidewinder"),
//    Ship("cobra"),
//    Ship("viper"),
//    Ship("eagle"),
//    Ship("vulture"),
//    Ship("dropship", Some("Drop it while its hot")),
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
//class ShipRequestBuilder(indexName: String = "bulkindexsubint") extends RequestBuilder[Ship] {
//
//  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
//  import ElasticDsl._
//
//  override def request(ship: Ship): BulkCompatibleDefinition = {
//    indexInto(s"$indexName/ships") source ship
//  }
//}
//
//object ShipPublisher extends Publisher[Ship] {
//
//  override def subscribe(s: Subscriber[_ >: Ship]): Unit = {
//    var remaining = Ship.ships
//    s.onSubscribe(new Subscription {
//      override def cancel(): Unit = ()
//      override def request(n: Long): Unit = {
//        remaining.take(n.toInt).foreach(t => s.onNext(t))
//        remaining = remaining.drop(n.toInt)
//        if (remaining.isEmpty)
//          s.onComplete()
//      }
//    })
//  }
//}
//
//object ShipEndlessPublisher extends Publisher[Ship] {
//
//  override def subscribe(s: Subscriber[_ >: Ship]): Unit = {
//    var remaining = Ship.ships
//    s.onSubscribe(new Subscription {
//      override def cancel(): Unit = ()
//      override def request(n: Long): Unit = {
//        remaining.take(n.toInt).foreach(t => s.onNext(t))
//        remaining = remaining.drop(n.toInt)
//      }
//    })
//  }
//}
//
//case class Ship(name: String, description: Option[String] = None, size: Int = Random.nextInt(100))
