package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.{Aggregations, DateHistogramInterval}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class SerialDiffPipelineAggHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("serialdiffagg")
    }.await
  }

  client.execute {
    createIndex("serialdiffagg") mappings {
      mapping("sales") fields(
        dateField("date"),
        doubleField("value").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("serialdiffagg/sales") fields("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("serialdiffagg/sales") fields("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("serialdiffagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("serialdiffagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("serialdiffagg/sales") fields("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("serialdiffagg/sales") fields("date" -> "2017-03-02", "value" -> 3000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "serial diff pipeline agg" - {
    "should return the expected diff values" in {

      val resp = client.execute {
        search("serialdiffagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .interval(DateHistogramInterval.Month)
            .subaggs(
              sumAgg("sales", "value"),
              diffAggregation("diff", "sales")
                .lag(1)
            )
        )
      }.await.result

      resp.totalHits shouldBe 6

      val buckets = resp.aggs.dateHistogram("sales_per_month").buckets

      buckets.size shouldBe 3
      buckets.head.data.contains("diff") shouldBe false
      Aggregations(buckets(1).data).serialDiff("diff").value shouldBe 2000.0
      Aggregations(buckets(2).data).serialDiff("diff").value shouldBe 2000.0
    }
  }
}
