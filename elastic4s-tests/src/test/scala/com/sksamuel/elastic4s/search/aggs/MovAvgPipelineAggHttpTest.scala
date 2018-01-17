package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.search.Aggregations
import com.sksamuel.elastic4s.searches.DateHistogramInterval
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, DockerTests}
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class MovAvgPipelineAggHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    http.execute {
      deleteIndex("movavgbucketagg")
    }.await
  }

  http.execute {
    createIndex("movavgbucketagg") mappings {
      mapping("sales") fields(
        dateField("date"),
        doubleField("value").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("movavgbucketagg/sales") fields("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("movavgbucketagg/sales") fields("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("movavgbucketagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("movavgbucketagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("movavgbucketagg/sales") fields("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("movavgbucketagg/sales") fields("date" -> "2017-03-02", "value" -> 3000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "moving avg pipeline agg" - {
    "should return the expected moving avg value" in {

      val resp = http.execute {
        search("movavgbucketagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .interval(DateHistogramInterval.Month)
            .subaggs (
              sumAgg("the_sum", "value"),
              movingAverageAggregation("the_movavg", "the_sum")
            )
        )
      }.await.right.get.result

      resp.totalHits shouldBe 6

      val buckets = resp.aggs.dateHistogram("sales_per_month").buckets

      buckets.size shouldBe 3
      buckets.head.data.contains("the_movavg") shouldBe false
      Aggregations(buckets(1).data).movAvg("the_movavg").value shouldBe 2000
      Aggregations(buckets(2).data).movAvg("the_movavg").value shouldBe 3000
    }
  }
}
