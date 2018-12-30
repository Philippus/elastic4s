package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.{DateHistogramBucket, DateHistogramInterval}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class DateHistogramAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("datehistaggs")
    }.await
  }

  client.execute {
    createIndex("datehistaggs") mappings {
      mapping("tv") fields(
        textField("name").fielddata(true),
        dateField("premiere_date").format("dd/MM/yyyy")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("datehistaggs/tv") fields("name" -> "Breaking Bad", "premiere_date" -> "20/01/2008"),
      indexInto("datehistaggs/tv") fields("name" -> "Better Call Saul", "premiere_date" -> "15/01/2008"),
      indexInto("datehistaggs/tv") fields("name" -> "Star Trek Discovery", "premiere_date" -> "27/06/2008"),
      indexInto("datehistaggs/tv") fields("name" -> "Game of Thrones", "premiere_date" -> "01/06/2008"),
      indexInto("datehistaggs/tv") fields("name" -> "Designated Survivor", "premiere_date" -> "12/03/2008"),
      indexInto("datehistaggs/tv") fields("name" -> "Walking Dead", "premiere_date" -> "19/01/2008")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "date histogram agg" - {
    "should return docs grouped by histogram interval" in {

      val resp = client.execute {
        search("datehistaggs").matchAllQuery().aggs {
          dateHistogramAgg("agg1", "premiere_date").interval(DateHistogramInterval.Month)
            .extendedBounds(ExtendedBounds("01/12/2007", "01/07/2008"))
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.dateHistogram("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        DateHistogramBucket("01/12/2007", 1196467200000L, 0, Map.empty),
        DateHistogramBucket("01/01/2008", 1199145600000L, 3, Map.empty),
        DateHistogramBucket("01/02/2008", 1201824000000L, 0, Map.empty),
        DateHistogramBucket("01/03/2008", 1204329600000L, 1, Map.empty),
        DateHistogramBucket("01/04/2008", 1207008000000L, 0, Map.empty),
        DateHistogramBucket("01/05/2008", 1209600000000L, 0, Map.empty),
        DateHistogramBucket("01/06/2008", 1212278400000L, 2, Map.empty),
        DateHistogramBucket("01/07/2008", 1214870400000L, 0, Map.empty)
      )
    }

    "should return keyed docs grouped by histogram interval" in {

      val resp = client.execute {
        search("datehistaggs").matchAllQuery().aggs {
          dateHistogramAgg("agg1", "premiere_date").interval(DateHistogramInterval.Month)
            .extendedBounds(ExtendedBounds("01/12/2007", "01/07/2008"))
            .keyed(true)
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.dateHistogram("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).sortBy(_.timestamp) shouldBe Seq(
        DateHistogramBucket("01/12/2007", 1196467200000L, 0, Map.empty),
        DateHistogramBucket("01/01/2008", 1199145600000L, 3, Map.empty),
        DateHistogramBucket("01/02/2008", 1201824000000L, 0, Map.empty),
        DateHistogramBucket("01/03/2008", 1204329600000L, 1, Map.empty),
        DateHistogramBucket("01/04/2008", 1207008000000L, 0, Map.empty),
        DateHistogramBucket("01/05/2008", 1209600000000L, 0, Map.empty),
        DateHistogramBucket("01/06/2008", 1212278400000L, 2, Map.empty),
        DateHistogramBucket("01/07/2008", 1214870400000L, 0, Map.empty)
      )
    }
  }
}
