package com.sksamuel.elastic4s.streams

import java.util.concurrent.{CountDownLatch, TimeUnit}

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.requests.indexes.IndexRequest
import com.sksamuel.elastic4s.requests.searches.SearchHit
import com.sksamuel.elastic4s.testkit.DockerTests
import org.reactivestreams.{Subscriber, Subscription}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ScrollPublisherIntegrationTest extends WordSpec with DockerTests with Matchers  {

  import ReactiveElastic._
  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  private val indexName = getClass.getSimpleName.toLowerCase
  private val indexType = "emperor"

  private implicit val system: ActorSystem = ActorSystem()

  val emperors = Array(
    Item("Augustus"),
    Item("Tiberius"),
    Item("Caligua"),
    Item("Claudius"),
    Item("Nero"),
    Item("Galba"),
    Item("Otho"),
    Item("Vitellius"),
    Item("Vespasian"),
    Item("Titus"),
    Item("Domitian"),
    Item("Nerva"),
    Item("Trajan"),
    Item("Hadrian"),
    Item("Antoninus Pius"),
    Item("Marcus Aurelius"),
    Item("Commodus"),
    Item("Pertinax"),
    Item("Diocletion")
  )

  implicit object RichSearchHitRequestBuilder extends RequestBuilder[SearchHit] {
    override def request(hit: SearchHit): IndexRequest = {
      indexInto(indexName / indexType).doc(hit.sourceAsString)
    }
  }

  Try {
    client.execute {
      createIndex(indexName)
    }.await
  }

  client.execute {
    bulk(emperors.map(indexInto(indexName / indexType).source(_))).refreshImmediately
  }.await

  "elastic-streams" should {
    "publish all data from the index" in {

      val publisher = client.publisher(search(indexName) query "*:*" scroll "1m")

      val completionLatch = new CountDownLatch(1)
      val documentLatch = new CountDownLatch(emperors.length)

      publisher.subscribe(new Subscriber[SearchHit] {
        override def onComplete(): Unit = completionLatch.countDown()
        override def onError(t: Throwable): Unit = fail(t)
        override def onSubscribe(s: Subscription): Unit = s.request(1000)
        override def onNext(t: SearchHit): Unit = documentLatch.countDown()
      })

      completionLatch.await(10, TimeUnit.SECONDS) shouldBe true
      documentLatch.await(10, TimeUnit.SECONDS) shouldBe true
    }
  }
}
