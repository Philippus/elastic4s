//package com.sksamuel.elastic4s.streams
//
//import java.util.concurrent.Executors
//
//import akka.actor.ActorSystem
//import com.sksamuel.elastic4s.Indexes
//import com.sksamuel.elastic4s.bulk.BulkCompatibleDefinition
//import com.sksamuel.elastic4s.http.ElasticDsl
//import com.sksamuel.elastic4s.jackson.ElasticJackson
//import com.sksamuel.elastic4s.testkit.DockerTests
//import org.reactivestreams.{Publisher, Subscriber, Subscription}
//import org.scalatest.{Matchers, WordSpec}
//
//import scala.concurrent.duration._
//import scala.util.Try
//
//class SubscriberFlushAfterTest extends WordSpec with Matchers with DockerTests {
//
//  import ElasticJackson.Implicits._
//  import ReactiveElastic._
//
//  implicit val system: ActorSystem = ActorSystem()
//
//  implicit object SpaceshipRequestBuilder extends RequestBuilder[Spaceship] {
//    override def request(ship: Spaceship): BulkCompatibleDefinition = {
//      indexInto("subscriberflushaftertest" / "ships").source(ship)
//    }
//  }
//
//  def deleteIndx(name: String): Unit = Try {
//    http.execute {
//      ElasticDsl.deleteIndex(name)
//    }.await
//  }
//
//  def freshIndex(index: String): Unit = {
//    deleteIndx(index)
//    ensureIndexExists(index)
//    blockUntilEmpty(index)
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
//  def blockUntilExactCount(expected: Long, index: String, types: String*): Unit = {
//    blockUntil(s"Expected count of $expected") { () =>
//      expected == http.execute {
//        search(index / types).size(0)
//      }.await.right.get.result.totalHits
//    }
//  }
//
//  def blockUntilEmpty(index: String): Unit = {
//    blockUntil(s"Expected empty index $index") { () =>
//      http.execute {
//        search(Indexes(index)).size(0)
//      }.await.right.get.result.totalHits == 0
//    }
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
//  deleteIndx("subscriberflushaftertest")
//  ensureIndexExists("subscriberflushaftertest")
//
//  "Reactive streams subscriber" should {
//    "send request if no document received within flush after duration" in {
//      freshIndex("subscriberflushaftertest1")
//
//      // batch size is 100, but we've made our publisher send only 1 every 3 seconds, so that we'll
//      // aways have insufficient to complete the batch before the flushAfter kicks in
//
//      val duration = 1.second
//      blockUntilExactCount(0, "subscriberflushaftertest1")
//
//      val config = SubscriberConfig[Spaceship](flushAfter = Some(duration), batchSize = 100)
//      val subscriber = http.subscriber[Spaceship](config)
//      new SpaceshipSlowPublisher(3.seconds).subscribe(subscriber)
//
//      // after the flushAfter duration, the flush after should have sent the request
//      blockUntilExactCount(1, "subscriberflushaftertest1")
//      // after another flushAfter duration, the next flush after should have sent the next request
//      blockUntilExactCount(2, "subscriberflushaftertest1")
//
//      subscriber.close()
//    }
//    "reset flush after interval each time a document is received" in {
//      freshIndex("subscriberflushaftertest2")
//
//      // batch size is 100, but we only have 5. Since the flushAfter interval is longer than the
//      // publication interval, it should only take effect once no more documents are published, this proving
//      // that each time a document is received, the flushAfter timer is reset.
//
//      val duration = 3.seconds
//      blockUntilExactCount(0, "subscriberflushaftertest2")
//
//      val config = SubscriberConfig[Spaceship](flushAfter = Some(duration), batchSize = 100)
//      val subscriber = http.subscriber[Spaceship](config)
//      new SpaceshipSlowPublisher(1.second).subscribe(subscriber)
//
//      // after 6 seconds, we should not have any documents yet as the flush after should still not have kicked in,
//      // even though all were published by now
//      Thread.sleep(6000)
//      blockUntilExactCount(0, "subscriberflushaftertest2")
//
//      // then shortly after, the flushAfter should kick in
//      blockUntilExactCount(5, "subscriberflushaftertest2")
//    }
//  }
//}
//
//
//class SpaceshipSlowPublisher(duration: FiniteDuration) extends Publisher[Spaceship] {
//
//  override def subscribe(s: Subscriber[_ >: Spaceship]): Unit = {
//    val sub = new Subscription {
//      private val executor = Executors.newSingleThreadExecutor()
//      private var remaining = Spaceship.ships
//      override def cancel(): Unit = executor.shutdownNow()
//      override def request(ignored: Long): Unit = {
//        executor.submit(new Runnable {
//          override def run(): Unit = {
//            while (remaining.nonEmpty) {
//              remaining.take(1).foreach(t => s.onNext(t))
//              remaining = remaining.drop(1)
//              Thread.sleep(duration.toMillis)
//            }
//          }
//        })
//      }
//    }
//    s.onSubscribe(sub)
//  }
//}
//
//
//case class Spaceship(name: String)
//
//
//object Spaceship {
//
//  val ships = List(
//    Spaceship("clipper"),
//    Spaceship("anaconda"),
//    Spaceship("courier"),
//    Spaceship("python"),
//    Spaceship("viper")
//  )
//}
