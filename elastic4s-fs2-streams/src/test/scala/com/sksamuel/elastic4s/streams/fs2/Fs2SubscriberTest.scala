package com.sksamuel.elastic4s.streams.fs2

import cats.effect.IO
import com.sksamuel.elastic4s.searches.SearchRequest
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest._
import scala.util.Try
import scala.concurrent.duration._

case class Item(name: String)

class Fs2SubscriberTest extends WordSpec with DockerTests with Matchers with BeforeAndAfterAll {
  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  private val indexName = getClass.getSimpleName.toLowerCase
  private val indexType = "emperor"

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

  override protected def beforeAll(): Unit = {
    Try {
      client.execute {
        createIndex(indexName)
      }.await
    }

    client.execute {
      bulk(emperors.map(indexInto(indexName / indexType).source(_))).refreshImmediately
    }.await
  }

  import com.sksamuel.elastic4s.cats.effect.instances._
  val subscriber = new Fs2Subscriber[IO](client)

  "Fs2Subscriber" should {

//    "reject a request if it doesn't have keep-alive set" in {
//      val searchRequest = search(indexName) query "*:*"
//      subscriber.stream[IO](searchRequest).compile.toList.unsafeRunSync() should have size emperors.length
//    }

    "get all the results" in {
      val searchRequest = search(indexName) query "*:*" scroll 20.seconds limit 2
      subscriber.stream(searchRequest).take(emperors.length).compile.toList.unsafeRunSync() should have size emperors.length
    }
  }

}
