package com.sksamuel.elastic4s.akka.reactivestreams

import akka.actor.ActorSystem
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.requests.searches.SearchHit
import com.sksamuel.elastic4s.testkit.DockerTests
import org.reactivestreams.Publisher
import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
import org.scalatestplus.testng.TestNGSuiteLike

import scala.util.Try

class PaginatedPublisherSearchAfterVerificationTest
    extends PublisherVerification[SearchHit](
      new TestEnvironment(DEFAULT_TIMEOUT_MILLIS),
      PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS
    ) with TestNGSuiteLike with DockerTests {

  import ElasticJackson.Implicits._
  import ReactiveElastic._

  implicit val system: ActorSystem = ActorSystem()

  Try {
    client.execute {
      deleteIndex("searchafterpubver")
    }.await
  }

  Try {
    client.execute {
      createIndex("searchafterpubver")
    }.await
  }

  client.execute {
    bulk(
      indexInto("searchafterpubver") source Empire("Parthian", "Persia", "Ctesiphon"),
      indexInto("searchafterpubver") source Empire("Ptolemaic", "Egypt", "Alexandria"),
      indexInto("searchafterpubver") source Empire("British", "Worldwide", "London"),
      indexInto("searchafterpubver") source Empire("Achaemenid", "Persia", "Babylon"),
      indexInto("searchafterpubver") source Empire("Sasanian", "Persia", "Ctesiphon"),
      indexInto("searchafterpubver") source Empire("Mongol", "East Asia", "Avarga"),
      indexInto("searchafterpubver") source Empire("Roman", "Mediterranean", "Rome"),
      indexInto("searchafterpubver") source Empire("Sumerian", "Mesopotamia", "Uruk"),
      indexInto("searchafterpubver") source Empire("Klingon", "Space", "Kronos"),
      indexInto("searchafterpubver") source Empire("Romulan", "Space", "Romulus"),
      indexInto("searchafterpubver") source Empire("Cardassian", "Space", "Cardassia Prime"),
      indexInto("searchafterpubver") source Empire("Egyptian", "Egypt", "Memphis"),
      indexInto("searchafterpubver") source Empire("Babylonian", "Levant", "Babylon")
    ).refreshImmediately
  }.await

  private val query = search("searchafterpubver").matchAllQuery().sortBy(fieldSort("_doc")).limit(2)

  override def boundedDepthOfOnNextAndRequestRecursion: Long = 2L

  override def createFailedPublisher(): Publisher[SearchHit] = null

  override def createPublisher(elements: Long): Publisher[SearchHit] = {
    new PaginatedPublisher(client, query, elements, SearchAfter)
  }
}
