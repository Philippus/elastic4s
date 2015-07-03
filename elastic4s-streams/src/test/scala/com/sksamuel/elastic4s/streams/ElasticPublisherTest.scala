package com.sksamuel.elastic4s.streams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.{ElasticDsl, RichSearchHit}
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.reactivestreams.{Subscriber, Publisher}
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}

class ElasticPublisherTest
  extends PublisherVerification[RichSearchHit](
    new TestEnvironment(DEFAULT_TIMEOUT_MILLIS),
    PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS
  ) with ElasticSugar {

  import ElasticDsl._
  import ElasticJackson.Implicits._

  implicit val system = ActorSystem()

  client.execute {
    create index "streams"
  }.await

  client.execute {
    bulk(
      index into "streams" / "empires" source Empire("Parthian", "Persia", "Ctesiphon"),
      index into "streams" / "empires" source Empire("Ptolemaic", "Egypt", "Alexandria"),
      index into "streams" / "empires" source Empire("British", "Worldwide", "London"),
      index into "streams" / "empires" source Empire("Achaemenid", "Persia", "Babylon"),
      index into "streams" / "empires" source Empire("Sasanian", "Persia", "Ctesiphon"),
      index into "streams" / "empires" source Empire("Mongol", "East Asia", "Avarga"),
      index into "streams" / "empires" source Empire("Roman", "Mediterranean", "Rome"),
      index into "streams" / "empires" source Empire("Sumerian", "Mesopotamia", "Uruk"),
      index into "streams" / "empires" source Empire("Klingon", "Space", "Kronos"),
      index into "streams" / "empires" source Empire("Romulan", "Space", "Romulus"),
      index into "streams" / "empires" source Empire("Cardassian", "Space", "Cardassia Prime"),
      index into "streams" / "empires" source Empire("Egyptian", "Egypt", "Memphis"),
      index into "streams" / "empires" source Empire("Babylonian", "Levant", "Babylon")
    )
  }

  blockUntilCount(2, "streams")

  val query = search in "streams" query "*:*" scroll "1m" limit 2

  override def boundedDepthOfOnNextAndRequestRecursion: Long = 2l

  override def createFailedPublisher(): Publisher[RichSearchHit] = null

  new Publisher[RichSearchHit] {
    def subscribe(s: Subscriber[_ >: RichSearchHit]) {
      s.onError(new RuntimeException("Can't subscribe subscriber: " + s + ", because of reasons."))
    }
  }

  override def createPublisher(elements: Long): Publisher[RichSearchHit] = {
    new ElasticPublisher(client, query, elements)
  }
}

case class Empire(name: String, location: String, capital: String)
