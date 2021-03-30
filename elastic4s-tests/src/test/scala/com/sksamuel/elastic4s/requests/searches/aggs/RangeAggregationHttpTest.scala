package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.aggs.responses.RangeBucket
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.BeforeAndAfterAll
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class RangeAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers with BeforeAndAfterAll {

  deleteIdx("rangeaggs")

  client.execute {
    createIndex("rangeaggs") mapping {
      mapping(
        textField("name").fielddata(true),
        intField("grade")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("rangeaggs").fields("name" -> "Breaking Bad", "grade" -> 9),
      indexInto("rangeaggs").fields("name" -> "Better Call Saul", "grade" -> 9),
      indexInto("rangeaggs").fields("name" -> "Star Trek Discovery", "grade" -> 7),
      indexInto("rangeaggs").fields("name" -> "Game of Thrones", "grade" -> 8),
      indexInto("rangeaggs").fields("name" -> "Designated Survivor", "grade" -> 6),
      indexInto("rangeaggs").fields("name" -> "Walking Dead", "grade" -> 5)
    ).refreshImmediately
  ).await

  "range agg" - {
    "should aggregate ranges" in {
      val resp = client.execute {
        search("rangeaggs").matchAllQuery().aggs {
          rangeAgg("agg1", "grade")
              .unboundedTo("meh", to = 5.5)
              .range("cool", from = 5.5, to = 7.5)
              .unboundedFrom("awesome", from = 7.5)
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.range("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        RangeBucket(Some("meh"), None, Some(5.5), 1, Map.empty),
        RangeBucket(Some("cool"), Some(5.5), Some(7.5), 2, Map.empty),
        RangeBucket(Some("awesome"), Some(7.5), None, 3, Map.empty)
      )
    }
  }
}
