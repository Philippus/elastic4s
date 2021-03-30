package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.requests.searches.aggs.responses.Aggregations
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class MovAvgPipelineAggHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("movavgbucketagg")
    }.await
  }

  client.execute {
    createIndex("movavgbucketagg") mapping {
      mapping(
        dateField("date"),
        doubleField("value").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("movavgbucketagg") fields("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("movavgbucketagg") fields("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("movavgbucketagg") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("movavgbucketagg") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("movavgbucketagg") fields("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("movavgbucketagg") fields("date" -> "2017-03-02", "value" -> 3000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "moving avg pipeline agg" - {
    "should return the expected moving avg value" in {

      val resp = client.execute {
        search("movavgbucketagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .interval(DateHistogramInterval.Month)
            .subaggs (
              sumAgg("the_sum", "value"),
              movingAverageAggregation("the_movavg", "the_sum")
            )
        )
      }.await.result

      resp.totalHits shouldBe 6

      val buckets = resp.aggs.dateHistogram("sales_per_month").buckets

      buckets.size shouldBe 3
      buckets.head.data.contains("the_movavg") shouldBe false
      Aggregations(buckets(1).data).movAvg("the_movavg").value shouldBe 2000
      Aggregations(buckets(2).data).movAvg("the_movavg").value shouldBe 3000
    }
  }
}
