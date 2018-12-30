package com.sksamuel.elastic4s.streams

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.bulk.BulkCompatibleRequest
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.util.Random

//class BulkIndexingSubscriberIntegrationTest extends WordSpec with DockerTests with Matchers with BeforeAndAfter {
//
//  import ReactiveElastic._
//
//  import scala.concurrent.duration._
//
//  implicit val system: ActorSystem = ActorSystem()
//
//  val indexName = "bulkindexsubint"
//  val strictIndex = "bulkindexfail"
//
//  def deleteIndx(name: String): Unit = Try {
//    http.execute {
//      ElasticDsl.deleteIndex(name)
//    }.await
//  }
//
//  after {
//    deleteIndx(indexName)
//    deleteIndx(strictIndex)
//  }
//
//  def blockUntilCount(expected: Long, index: String): Unit = {
//    blockUntil(s"Expected count of $expected") { () =>
//      val result = http.execute {
//        search(index).matchAllQuery().size(0)
//      }.await.right.get
//      expected <= result.result.totalHits
//    }
//  }
//
//  @deprecated
//  def blockUntilCount(expected: Long, indexAndTypes: IndexAndTypes): Unit = {
//    blockUntil(s"Expected count of $expected") { () =>
//      val result = http.execute {
//        search(indexAndTypes).matchAllQuery().size(0)
//      }.await.right.get
//      expected <= result.result.totalHits
//    }
//  }
//
//  def blockUntil(explain: String)(predicate: () => Boolean): Unit = {
//
//    var backoff = 0
//    var done = false
//
//    while (backoff <= 16 && !done) {
//      if (backoff > 0) Thread.sleep(200 * backoff)
//      backoff = backoff + 1
//      try {
//        done = predicate()
//      } catch {
//        case e: Throwable =>
//          logger.warn("problem while testing predicate", e)
//      }
//    }
//
//    require(done, s"Failed waiting on: $explain")
//  }
//
//  def ensureIndexExists(index: String): Unit = {
//    Try {
//      http.execute {
//        createIndex(index)
//      }.await
//    }
//  }
//
//  Try {
//    http.execute {
//      deleteIndex(indexName)
//    }.await
//  }
//
//  Try {
//    http.execute {
//      createIndex(indexName)
//    }.await
//  }
//
//  "elastic-streams" should {
//    "index all received data" in {
//      ensureIndexExists(indexName)
//      implicit val builder: ShipRequestBuilder = new ShipRequestBuilder(indexName)
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
//      implicit val builder: ShipRequestBuilder = new ShipRequestBuilder(indexName)
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
//
//      implicit val builder: ShipRequestBuilder = new ShipRequestBuilder(strictIndex)
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
object Ship {

  val ships = List(
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

  import ElasticDsl._
  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  override def request(ship: Ship): BulkCompatibleRequest = {
    indexInto(s"$indexName/ships") source ship
  }
}

object ShipPublisher extends Publisher[Ship] {

  override def subscribe(s: Subscriber[_ >: Ship]): Unit = {
    var remaining = Ship.ships
    s.onSubscribe(new Subscription {
      override def cancel(): Unit = ()
      override def request(n: Long): Unit = {
        remaining.take(n.toInt).foreach(t => s.onNext(t))
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
        remaining.take(n.toInt).foreach(t => s.onNext(t))
        remaining = remaining.drop(n.toInt)
      }
    })
  }
}

case class Ship(name: String, description: Option[String] = None, size: Int = Random.nextInt(100))
