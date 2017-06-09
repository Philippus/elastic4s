//package com.sksamuel.elastic4s.streams
//
//import java.util.concurrent.{CountDownLatch, TimeUnit}
//
//import akka.actor.ActorSystem
//import com.sksamuel.elastic4s.ElasticsearchClientUri
//import com.sksamuel.elastic4s.bulk.RichBulkItemResponse
//import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
//import com.sksamuel.elastic4s.http.bulk.BulkResponseItem
//import com.sksamuel.elastic4s.testkit.{ElasticSugar, SharedElasticSugar}
//import org.scalatest.{Matchers, WordSpec}
//
//class SubscriberListenerTest extends WordSpec with Matchers with SharedElasticSugar with ElasticDsl {
//
//  import ReactiveElastic._
//
//  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))
//
//  implicit val system = ActorSystem()
//  implicit val builder = new ShipRequestBuilder()
//
//  ensureIndexExists("subscriberlistenertest")
//
//  "Reactive streams subscriber" should {
//    "invoke listener for each confirmed doc" in {
//
//      val latch = new CountDownLatch(Ship.ships.length)
//
//      val config = SubscriberConfig(listener = new ResponseListener[Ship] {
//        def onAck(resp: BulkResponseItem, original: Ship): Unit = latch.countDown()
//      })
//      val subscriber = http.subscriber[Ship](config)
//      ShipPublisher.subscribe(subscriber)
//
//      latch.await(1, TimeUnit.MINUTES) shouldBe true
//    }
//  }
//}
