package com.sksamuel.elastic4s.bulk

import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, ElasticSugar}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class BulkProcessorTest extends FlatSpec with Matchers with ElasticSugar with ClassloaderLocalNodeProvider {

  deleteIndex("books")

  client.execute {
    createIndex("books").mappings(
      mapping("plays")
    )
  }.await

  "bulk processor" should "insert all data" in {

    val processor = BulkProcessorBuilder().actionCount(3).concurrentRequests(2).build(client)

    processor.add(indexInto("books" / "plays").fields("name" -> "Midsummer Nights Dream"))
    processor.add(indexInto("books" / "plays").fields("name" -> "Cymbeline"))
    processor.add(indexInto("books" / "plays").fields("name" -> "Winters Tale"))
    processor.add(indexInto("books" / "plays").fields("name" -> "King John"))

    processor.close(10.seconds)
    blockUntilCount(4, "books")
  }

  it should "honour action count" in {

    val processor = BulkProcessorBuilder().actionCount(2).concurrentRequests(1).build(client)

    processor.add(indexInto("books" / "plays").fields("name" -> "Moby Dick"))
    processor.add(indexInto("books" / "plays").fields("name" -> "Uncle Toms Cabin"))

    blockUntilCount(6, "books" / "plays")

    processor.add(indexInto("books" / "plays").fields("name" -> "Life of Pi"))
    processor.add(indexInto("books" / "plays").fields("name" -> "Catcher in the Rye"))

    blockUntilCount(8, "books" / "plays")
    Await.ready(processor.close(), 10.seconds)
  }

}
