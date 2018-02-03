package com.sksamuel.elastic4s.streams

import java.util.concurrent.{CountDownLatch, TimeUnit}

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.http.search.SearchHit
import com.sksamuel.elastic4s.indexes.IndexRequest
import com.sksamuel.elastic4s.searches.RichSearchHit
import com.sksamuel.elastic4s.testkit.DockerTests
import org.elasticsearch.ResourceAlreadyExistsException
import org.elasticsearch.transport.RemoteTransportException
import org.reactivestreams.{Subscriber, Subscription}
import org.scalatest.{Matchers, WordSpec}

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

  implicit object RichSearchHitRequestBuilder extends RequestBuilder[RichSearchHit] {
    override def request(hit: RichSearchHit): IndexRequest = {
      indexInto(indexName / indexType).doc(hit.sourceAsString)
    }
  }

  try {
    http.execute {
      createIndex(indexName)
    }.await
  } catch {
    case _: ResourceAlreadyExistsException => // Ok, ignore.
    case _: RemoteTransportException => // Ok, ignore.
  }

  http.execute {
    bulk(emperors.map(indexInto(indexName / indexType).source(_))).refreshImmediately
  }.await

  "elastic-streams" should {
    "publish all data from the index" in {

      val publisher = http.publisher(search(indexName) query "*:*" scroll "1m")

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
