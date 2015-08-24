package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl.{index, _}
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import concurrent.duration._
import com.sksamuel.elastic4s.testkit.ElasticSugar

/**
 * Tests for IterableSearch
 */
class IterableSearchTest extends WordSpec with MockitoSugar with ElasticSugar with Matchers {

  val indexName = getClass.getSimpleName.toLowerCase

  val expectedRecords = 100

  // set up our data
  {
    val bulkResp = client.execute {
      bulk(
            (0 until expectedRecords).map { i =>
              index into s"$indexName/foo" fields ("record" -> s"record $i")
            }
          )
    }.await

    require(!bulkResp.hasFailures, "test setup failed")
    blockUntilCount(expectedRecords, indexName)

  }

  val query = search in indexName / "foo" query matchall

  "IterableSearch.iterate" should {
    "iterate all search results on demand" in {

      // ------------------------------------------------------------------------------------------------
      // setup - wrap the client to check when the calls are made
      // ------------------------------------------------------------------------------------------------
      val underlyingClient = client
      var executeCount = 0
      object TestClient extends ElasticClient(client.java) {
        override def execute[T, R, Q](t: T)(implicit executable: Executable[T, R, Q]) = {
          executeCount = executeCount + 1
          underlyingClient.execute(t)
        }
      }

      val querySize = 5
      // ------------------------------------------------------------------------------------------------
      // call our method under test
      // ------------------------------------------------------------------------------------------------
      implicit val timeout = (2 seconds)
      val hitsIterable = IterableSearch(TestClient).iterate(query.size(querySize))

      // ------------------------------------------------------------------------------------------------
      // assert the iterable makes queries on-demand and produced the correct amount of results
      // make a normal query to get our expected hits
      // ------------------------------------------------------------------------------------------------
      val expectedHits = client.execute(query).await.totalHits
      expectedHits shouldBe expectedRecords // our query should retrieve the records we put in

      // let's keep track of all the results in a lazy stream (so we can call .size)
      val stream = hitsIterable.toStream
      executeCount shouldBe 1

      stream.drop(3)
      executeCount shouldBe 1

      stream.drop(9)
      executeCount shouldBe 2

      stream.drop(9)
      executeCount shouldBe 2

      stream.drop(10)
      executeCount shouldBe 3

      stream.drop(50)
      executeCount shouldBe 11

      stream.size shouldBe expectedRecords

      // the + 1 is the last query which returns a null scroll id, showing us there are no more results
      val totalQueries = (expectedRecords / querySize) + 1
      executeCount shouldBe totalQueries
    }

  }
}
