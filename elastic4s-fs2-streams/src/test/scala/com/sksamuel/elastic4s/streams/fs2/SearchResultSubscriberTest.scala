package com.sksamuel.elastic4s.streams.fs2

import cats.effect.IO
import cats.instances.list._
import cats.instances.try_._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.traverse._
import com.sksamuel.elastic4s.http.search.SearchHit
import com.sksamuel.elastic4s.testkit.DockerTests
import fs2.Stream
import org.scalacheck._
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.concurrent.duration._

case class Item(name: String)

class SearchResultSubscriberTest
    extends WordSpec
    with DockerTests
    with Matchers
    with GeneratorDrivenPropertyChecks
    with BeforeAndAfterAll {
  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  private val indexName = getClass.getSimpleName.toLowerCase
  private val indexType = "emperor-fs2"

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
    Try(client.execute(deleteIndex(indexName)).await)
    Try(client.execute(createIndex(indexName)).await)

    client.execute {
      bulk(emperors.map(indexInto(indexName / indexType).source(_))).refreshImmediately
    }.await
  }

  override protected def afterAll(): Unit = {
    Try(client.execute(deleteIndex(indexName)).await)
  }

  import com.sksamuel.elastic4s.cats.effect.instances._
  val subscriber = new SearchResultSubscriber[IO](client)

  "SearchResultSubscriber" should {
    "scrolls through all the results" in {
      val keepAliveGen: Gen[Option[FiniteDuration]] =
        Gen.oneOf(List(
          None,
          Some(5.seconds),
          Some(1.minute))
        )
      val limitPerPageGen: Gen[Int] =
        Gen.oneOf(
          (1 to emperors.length + 1).toList ++
            List(50, 100)
        )

      forAll((keepAliveGen, "keepAlive[Option]"), (limitPerPageGen, "limitPerPage")) {
        (keepAliveMaybe: Option[FiniteDuration], limitPerPage: Int) =>
          val searchRequest = keepAliveMaybe.foldLeft {
            search(indexName).query("*:*").limit(limitPerPage)
          }(_ scroll _)

          val results = subscriber
            .stream(searchRequest)
            .compile
            .toList
            .unsafeRunSync()

          val resultsAsItems = results
            .traverse(_.safeTo[Item])
            .toEither
            .valueOr(fail("Error decoding items", _))

          resultsAsItems should contain theSameElementsAs emperors
      }
    }
  }

}
