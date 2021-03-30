package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class AvgBucketPipelineAggHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("avgbucketagg")
    }.await
  }

  client.execute {
    createIndex("avgbucketagg") mapping {
      properties(
        dateField("date"),
        doubleField("value").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("avgbucketagg") fields("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("avgbucketagg") fields("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("avgbucketagg") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("avgbucketagg") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("avgbucketagg") fields("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("avgbucketagg") fields("date" -> "2017-03-02", "value" -> 3000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "avg bucket pipeline agg" - {
    "should return the expected avg value" in {

      val resp = client.execute {
        search("avgbucketagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .interval(DateHistogramInterval.Month)
            .subaggs {
              sumAgg("sales", "value")
            },

          avgBucketAgg("avg_monthly_sales", "sales_per_month>sales")
        )
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.avgBucket("avg_monthly_sales")
      agg.value shouldBe 4000.0
    }
  }
}
