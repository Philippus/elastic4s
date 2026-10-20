package com.sksamuel.elastic4s.pekko.reactivestreams

import org.apache.pekko.actor.ActorSystem
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.requests.searches.SearchHit
import com.sksamuel.elastic4s.testkit.DockerTests
import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.scalatestplus.testng.TestNGSuiteLike

import scala.util.Try

class ScrollPublisherVerificationTest
    extends PublisherVerification[SearchHit](
      new TestEnvironment(DEFAULT_TIMEOUT_MILLIS),
      PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS
    ) with TestNGSuiteLike with DockerTests {

  import ElasticJackson.Implicits.*

  implicit val system: ActorSystem = ActorSystem()

  private val indexName = "scrollpubver-pekko"

  Try {
    client.execute {
      deleteIndex(indexName)
    }.await
  }

  Try {
    client.execute {
      createIndex(indexName)
    }.await
  }

  client.execute {
    bulk(
      indexInto(indexName).source(Empire("Parthian", "Persia", "Ctesiphon")),
      indexInto(indexName).source(Empire("Ptolemaic", "Egypt", "Alexandria")),
      indexInto(indexName).source(Empire("British", "Worldwide", "London")),
      indexInto(indexName).source(Empire("Achaemenid", "Persia", "Babylon")),
      indexInto(indexName).source(Empire("Sasanian", "Persia", "Ctesiphon")),
      indexInto(indexName).source(Empire("Mongol", "East Asia", "Avarga")),
      indexInto(indexName).source(Empire("Roman", "Mediterranean", "Rome")),
      indexInto(indexName).source(Empire("Sumerian", "Mesopotamia", "Uruk")),
      indexInto(indexName).source(Empire("Klingon", "Space", "Kronos")),
      indexInto(indexName).source(Empire("Romulan", "Space", "Romulus")),
      indexInto(indexName).source(Empire("Cardassian", "Space", "Cardassia Prime")),
      indexInto(indexName).source(Empire("Egyptian", "Egypt", "Memphis")),
      indexInto(indexName).source(Empire("Babylonian", "Levant", "Babylon"))
    ).refreshImmediately
  }.await

  private val query = search(indexName).matchAllQuery().scroll("1m").limit(2)

  override def boundedDepthOfOnNextAndRequestRecursion: Long = 2L

  override def createFailedPublisher(): Publisher[SearchHit] = null

  override def createPublisher(elements: Long): Publisher[SearchHit] = {
    new ScrollPublisher(client, query, elements)
  }
}

case class Empire(name: String, location: String, capital: String)
