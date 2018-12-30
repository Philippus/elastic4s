package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class ExtendedStatsBucketPipelineAggHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("extendedstatsbucketagg")
    }.await
  }

  client.execute {
    createIndex("extendedstatsbucketagg") mappings {
      mapping("sales") fields(
        dateField("date"),
        doubleField("value").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("extendedstatsbucketagg/sales") fields("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("extendedstatsbucketagg/sales") fields("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("extendedstatsbucketagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("extendedstatsbucketagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("extendedstatsbucketagg/sales") fields("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("extendedstatsbucketagg/sales") fields("date" -> "2017-03-02", "value" -> 3000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "extended stats bucket pipeline agg" - {
    "should return the expected extended stats values" in {

      val resp = client.execute {
        search("extendedstatsbucketagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .interval(DateHistogramInterval.Month)
            .subaggs {
              sumAgg("sales", "value")
            },

          extendedStatsBucketAggregation("stats_monthly_sales", "sales_per_month>sales")
        )
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.extendedStatsBucket("stats_monthly_sales")
      agg.count shouldBe 3
      agg.min shouldBe 2000.0
      agg.max shouldBe 6000.0
      agg.avg shouldBe 4000.0
      agg.sum shouldBe 12000.0
      agg.sumOfSquares shouldBe 5.6E7
      math.abs(agg.variance - 2666666.66) < 0.1 shouldBe true
      math.abs(agg.stdDeviation - 1632.99) < 0.1 shouldBe true
      math.abs(agg.stdDeviationBoundsLower - 734.01) < 0.1 shouldBe true
      math.abs(agg.stdDeviationBoundsUpper - 7265.98) < 0.1 shouldBe true
    }
  }
}
