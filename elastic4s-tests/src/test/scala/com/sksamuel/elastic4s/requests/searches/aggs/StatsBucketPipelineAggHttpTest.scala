package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class StatsBucketPipelineAggHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("statsbucketagg")
    }.await
  }

  client.execute {
    createIndex("statsbucketagg") mappings {
      mapping("sales") fields(
        dateField("date"),
        doubleField("value").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("statsbucketagg/sales") fields("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("statsbucketagg/sales") fields("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("statsbucketagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("statsbucketagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("statsbucketagg/sales") fields("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("statsbucketagg/sales") fields("date" -> "2017-03-02", "value" -> 3000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "stats bucket pipeline agg" - {
    "should return the expected stats values" in {

      val resp = client.execute {
        search("statsbucketagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .interval(DateHistogramInterval.Month)
            .subaggs {
              sumAgg("sales", "value")
            },

          statsBucketAggregation("stats_monthly_sales", "sales_per_month>sales")
        )
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.statsBucket("stats_monthly_sales")
      agg.count shouldBe 3
      agg.min shouldBe 2000.0
      agg.max shouldBe 6000.0
      agg.avg shouldBe 4000.0
      agg.sum shouldBe 12000.0
    }
  }
}
