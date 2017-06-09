//package com.sksamuel.elastic4s.streams
//
//import akka.actor.ActorSystem
//import com.sksamuel.elastic4s.ElasticsearchClientUri
//import com.sksamuel.elastic4s.http.search.SearchHit
//import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
//import com.sksamuel.elastic4s.jackson.ElasticJackson
//import com.sksamuel.elastic4s.searches.RichSearchHit
//import com.sksamuel.elastic4s.testkit.{ClassLocalNodeProvider, SharedElasticSugar}
//import org.reactivestreams.Publisher
//import org.reactivestreams.tck.{PublisherVerification, TestEnvironment}
//import org.scalatest.testng.TestNGSuiteLike
//
//class ScrollPublisherVerificationTest
//  extends PublisherVerification[SearchHit](
//    new TestEnvironment(DEFAULT_TIMEOUT_MILLIS),
//    PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS
//  ) with SharedElasticSugar with TestNGSuiteLike with ClassLocalNodeProvider with ElasticDsl {
//
//  import ElasticJackson.Implicits._
//
//  private val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))
//
//  implicit val system = ActorSystem()
//
//  ensureIndexExists("scrollpubver")
//
//  client.execute {
//    bulk(
//      indexInto("scrollpubver" / "empires")source Empire("Parthian", "Persia", "Ctesiphon"),
//      indexInto("scrollpubver" / "empires")source Empire("Ptolemaic", "Egypt", "Alexandria"),
//      indexInto("scrollpubver" / "empires")source Empire("British", "Worldwide", "London"),
//      indexInto("scrollpubver" / "empires")source Empire("Achaemenid", "Persia", "Babylon"),
//      indexInto("scrollpubver" / "empires")source Empire("Sasanian", "Persia", "Ctesiphon"),
//      indexInto("scrollpubver" / "empires")source Empire("Mongol", "East Asia", "Avarga"),
//      indexInto("scrollpubver" / "empires")source Empire("Roman", "Mediterranean", "Rome"),
//      indexInto("scrollpubver" / "empires")source Empire("Sumerian", "Mesopotamia", "Uruk"),
//      indexInto("scrollpubver" / "empires")source Empire("Klingon", "Space", "Kronos"),
//      indexInto("scrollpubver" / "empires")source Empire("Romulan", "Space", "Romulus"),
//      indexInto("scrollpubver" / "empires")source Empire("Cardassian", "Space", "Cardassia Prime"),
//      indexInto("scrollpubver" / "empires")source Empire("Egyptian", "Egypt", "Memphis"),
//      indexInto("scrollpubver" / "empires")source Empire("Babylonian", "Levant", "Babylon")
//    )
//  }
//
//  blockUntilCount(13, "scrollpubver")
//
//  private val query = search("scrollpubver") query "*:*" scroll "1m" limit 2
//
//  override def boundedDepthOfOnNextAndRequestRecursion: Long = 2l
//
//  override def createFailedPublisher(): Publisher[SearchHit] = null
//
//  override def createPublisher(elements: Long): Publisher[SearchHit] = {
//    new ScrollPublisher(http, query, elements)
//  }
//}
//
//case class Empire(name: String, location: String, capital: String)
