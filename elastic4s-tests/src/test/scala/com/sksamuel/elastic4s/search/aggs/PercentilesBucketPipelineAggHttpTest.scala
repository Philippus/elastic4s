package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.searches.DateHistogramInterval
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, DockerTests}
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class PercentilesBucketPipelineAggHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    http.execute {
      deleteIndex("percentilesbucketagg")
    }.await
  }

  http.execute {
    createIndex("percentilesbucketagg") mappings {
      mapping("sales") fields(
        dateField("date"),
        doubleField("value").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("percentilesbucketagg/sales") fields("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("percentilesbucketagg/sales") fields("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("percentilesbucketagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("percentilesbucketagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("percentilesbucketagg/sales") fields("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("percentilesbucketagg/sales") fields("date" -> "2017-03-02", "value" -> 3000.0),
      indexInto("percentilesbucketagg/sales") fields("date" -> "2017-04-02", "value" -> 4000.0),
      indexInto("percentilesbucketagg/sales") fields("date" -> "2017-04-02", "value" -> 4000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "percentiles bucket pipeline agg" - {
    "should return the expected percentile values" in {

      val resp = http.execute {
        search("percentilesbucketagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .interval(DateHistogramInterval.Month)
            .subaggs {
              sumAgg("sales", "value")
            },

          percentilesBucketAggregation("percentiles_monthly_sales", "sales_per_month>sales")
            .percents(Seq(25.0, 50.0, 75.0))
        )
      }.await.right.get.result

      resp.totalHits shouldBe 8

      val agg = resp.aggs.percentilesBucket("percentiles_monthly_sales")
      agg.values.size shouldBe 3
      agg.values("25.0") shouldBe 4000.0
      agg.values("50.0") shouldBe 6000.0
      agg.values("75.0") shouldBe 6000.0
    }
  }
}
