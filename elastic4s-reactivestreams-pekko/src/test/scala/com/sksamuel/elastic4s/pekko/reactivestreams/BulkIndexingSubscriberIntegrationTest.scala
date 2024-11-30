package com.sksamuel.elastic4s.pekko.reactivestreams

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.requests.bulk.BulkCompatibleRequest
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.util.Random

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
      override def cancel(): Unit         = ()
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
      override def cancel(): Unit         = ()
      override def request(n: Long): Unit = {
        remaining.take(n.toInt).foreach(t => s.onNext(t))
        remaining = remaining.drop(n.toInt)
      }
    })
  }
}

case class Ship(name: String, description: Option[String] = None, size: Int = Random.nextInt(100))
