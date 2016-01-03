package com.sksamuel.elastic4s.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.testkit.ElasticSugar
import com.sksamuel.elastic4s.{BulkCompatibleDefinition, ElasticDsl}
import org.reactivestreams.{Publisher, Subscriber, Subscription}
import org.scalatest.{Matchers, WordSpec}
import scala.concurrent.duration._

class SubscriberFlushAfterTest extends WordSpec with Matchers with ElasticSugar {

  import ElasticDsl._
  import ElasticJackson.Implicits._
  import ReactiveElastic._

  implicit val system = ActorSystem()


  implicit object SpaceshipRequestBuilder extends RequestBuilder[Spaceship] {
    override def request(ship: Spaceship): BulkCompatibleDefinition = {
      index into "subscriberflushaftertest" / "ships" source ship
    }
  }


  ensureIndexExists("subscriberflushaftertest")

  "Reactive streams subscriber" should {
    "send request if no document received within flush after duration" in {

      // batch size is 2, but we've made our publisher only ever send 1 per request, so that we'll
      // aways have insufficient to complete the batch. Therefore the flushAfter should kick in,
      // sending the partial batch to elasticsearch to be indexed.

      val flushAfterDuration = 3.seconds

      val config = SubscriberConfig(flushAfter = Some(flushAfterDuration), batchSize = 2)
      val subscriber = client.subscriber[Spaceship](config)
      SpaceshipPublisher.subscribe(subscriber)

      blockUntilCount(0, "subscriberflushaftertest")

      // after the flushAfterDuration, the flush after should have sent the request
      Thread.sleep(flushAfterDuration.toMillis)
      blockUntilCount(1, "subscriberflushaftertest")

      // after another flushAfterDuration, the next flush after should have sent the next request
      Thread.sleep(flushAfterDuration.toMillis)
      blockUntilCount(2, "subscriberflushaftertest")
    }
  }
}


object SpaceshipPublisher extends Publisher[Spaceship] {

  override def subscribe(s: Subscriber[_ >: Spaceship]): Unit = {
    var remaining = Spaceship.ships
    s.onSubscribe(new Subscription {
      override def cancel(): Unit = ()
      override def request(ignored: Long): Unit = {
        remaining.take(1).foreach(s.onNext)
        remaining = remaining.drop(1)
        if (remaining.isEmpty)
          s.onComplete()
      }
    })
  }
}


case class Spaceship(name: String)


object Spaceship {

  val ships = Array(
    Spaceship("clipper"),
    Spaceship("anaconda"),
    Spaceship("courier"),
    Spaceship("python"),
    Spaceship("fer-de-lance"),
    Spaceship("sidewinder"),
    Spaceship("cobra"),
    Spaceship("viper"),
    Spaceship("eagle"),
    Spaceship("vulture"),
    Spaceship("dropship"),
    Spaceship("orca"),
    Spaceship("type6"),
    Spaceship("type7"),
    Spaceship("type9"),
    Spaceship("hauler"),
    Spaceship("adder"),
    Spaceship("asp explorer"),
    Spaceship("diamondback")
  )

}