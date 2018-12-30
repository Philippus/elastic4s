package com.sksamuel.elastic4s.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.requests.bulk.BulkCompatibleRequest
import com.sksamuel.elastic4s.testkit.DockerTests
import org.reactivestreams.tck.SubscriberWhiteboxVerification.{SubscriberPuppet, WhiteboxSubscriberProbe}
import org.reactivestreams.tck.{SubscriberWhiteboxVerification, TestEnvironment}
import org.reactivestreams.{Subscriber, Subscription}
import org.scalatest.testng.TestNGSuiteLike

class BulkIndexingSubscriberWhiteboxTest
  extends SubscriberWhiteboxVerification[Item](new TestEnvironment(DEFAULT_TIMEOUT_MILLIS))
    with TestNGSuiteLike with DockerTests {

  implicit val system: ActorSystem = ActorSystem()

  try {
    client.execute {
      createIndex("bulkindexwhitebox")
    }.await
  } catch {
    case _: Exception =>
  }

  object ItemRequestBuilder extends RequestBuilder[Item] {

    import ElasticJackson.Implicits._

    override def request(t: Item): BulkCompatibleRequest = indexInto("bulkindexwhitebox" / "castles").doc(t)
  }

  override def createSubscriber(probe: WhiteboxSubscriberProbe[Item]): Subscriber[Item] = {
    new BulkIndexingSubscriber[Item](client, ItemRequestBuilder, SubscriberConfig()) {

      override def onSubscribe(s: Subscription): Unit = {
        super.onSubscribe(s)
        // register a successful Subscription, and create a Puppet,
        // for the WhiteboxVerification to be able to drive its tests:
        probe.registerOnSubscribe(new SubscriberPuppet() {

          def triggerRequest(elements: Long): Unit = {
            s.request(elements)
          }

          def signalCancel(): Unit = {
            s.cancel()
          }
        })
      }

      override def onComplete(): Unit = {
        super.onComplete()
        probe.registerOnComplete()
      }

      override def onError(t: Throwable): Unit = {
        probe.registerOnError(t)
      }

      override def onNext(t: Item): Unit = {
        super.onNext(t)
        probe.registerOnNext(t)
      }
    }
  }

  override def createElement(element: Int): Item = castles(element)

  val castles = Array(
    Item("bodium"),
    Item("hever"),
    Item("tower of london"),
    Item("canarvon"),
    Item("conwy"),
    Item("beaumaris"),
    Item("bolsover"),
    Item("conningsbrough"),
    Item("tintagel"),
    Item("rochester"),
    Item("dover"),
    Item("hexham"),
    Item("harleigh"),
    Item("white"),
    Item("radley"),
    Item("berkeley")
  )
}

case class Item(name: String)
