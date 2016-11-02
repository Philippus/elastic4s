package com.sksamuel.elastic4s2.streams

import java.util.concurrent.{TimeUnit, CountDownLatch}

import akka.actor.ActorSystem
import com.sksamuel.elastic4s2.{RichSearchHit, IndexDefinition, ElasticDsl2$}
import com.sksamuel.elastic4s2.jackson.ElasticJackson
import com.sksamuel.elastic4s2.testkit.ElasticSugar
import org.reactivestreams.{Subscription, Subscriber}
import org.scalatest.{Matchers, WordSpec}

class ScrollPublisherIntegrationTest extends WordSpec with ElasticSugar with Matchers {

  import ElasticDsl2._
  import ElasticJackson.Implicits._
  import ReactiveElastic._

  val indexName = getClass.getSimpleName.toLowerCase
  val indexType = "emperor"

  implicit val system = ActorSystem()

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
    override def request(hit: RichSearchHit): IndexDefinition = {
      index into indexName / indexType source hit.sourceAsString
    }
  }

  ensureIndexExists(indexName)

  client.execute {
    bulk(emperors.map(index into indexName / indexType source _))
  }.await

  blockUntilCount(emperors.length, indexName)

  "elastic-streams" should {
    "publish all data from the index" in {

      val publisher = client.publisher(search in indexName / indexType query "*:*" scroll "1m")

      val completionLatch = new CountDownLatch(1)
      val documentLatch = new CountDownLatch(emperors.length)

      publisher.subscribe(new Subscriber[RichSearchHit] {
        override def onComplete(): Unit = completionLatch.countDown()
        override def onError(t: Throwable): Unit = fail(t)
        override def onSubscribe(s: Subscription): Unit = s.request(1000)
        override def onNext(t: RichSearchHit): Unit = documentLatch.countDown()
      })
      client

      completionLatch.await(5, TimeUnit.SECONDS) should be (true)
      documentLatch.await(5, TimeUnit.SECONDS) should be (true)
    }
  }
}
