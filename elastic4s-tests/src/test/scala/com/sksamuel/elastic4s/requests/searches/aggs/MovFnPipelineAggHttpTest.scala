package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.requests.searches.aggs.responses.Aggregations
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Try

import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.DateHistogram

class MovFnPipelineAggHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("movfnbucketagg")
    }.await
  }

  client.execute {
    createIndex("movfnbucketagg") mapping {
      properties(
        dateField("date"),
        doubleField("value").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("movfnbucketagg") fields ("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("movfnbucketagg") fields ("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("movfnbucketagg") fields ("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("movfnbucketagg") fields ("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("movfnbucketagg") fields ("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("movfnbucketagg") fields ("date" -> "2017-03-02", "value" -> 3000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "moving fn pipeline agg" - {
    "should return the expected moving sum value" in {

      val resp = client.execute {
        search("movfnbucketagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .calendarInterval(DateHistogramInterval.Month)
            .subaggs(
              sumAgg("the_sum", "value"),
              movingFunctionAggregation("the_movfn", "the_sum", "MovingFunctions.sum(values)", 10)
            )
        )
      }.await.result

      resp.totalHits shouldBe 6

      val buckets = resp.aggs.result[DateHistogram]("sales_per_month").buckets

      buckets.size shouldBe 3
      buckets.head.data.contains("the_movfn") shouldBe true
      Aggregations(buckets(1).data).movFn("the_movfn").value shouldBe 2000
      Aggregations(buckets(2).data).movFn("the_movfn").value shouldBe 6000
    }
  }
}
