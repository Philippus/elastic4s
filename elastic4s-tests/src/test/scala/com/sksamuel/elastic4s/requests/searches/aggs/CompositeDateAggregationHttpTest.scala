package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.requests.searches.aggs.CompositeAggregation._
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class CompositeDateAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("compositedatehistaggs")
    }.await
  }

  client.execute {
    createIndex("compositedatehistaggs") mapping {
      properties(
        textField("name").fielddata(true),
        dateField("premiere_date").format("dd/MM/yyyy")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("compositedatehistaggs") fields("name" -> "Breaking Bad", "premiere_date" -> "20/01/2008"),
      indexInto("compositedatehistaggs") fields("name" -> "Better Call Saul", "premiere_date" -> "15/01/2008"),
      indexInto("compositedatehistaggs") fields("name" -> "Star Trek Discovery", "premiere_date" -> "27/06/2008"),
      indexInto("compositedatehistaggs") fields("name" -> "Game of Thrones", "premiere_date" -> "01/06/2008"),
      indexInto("compositedatehistaggs") fields("name" -> "Designated Survivor", "premiere_date" -> "12/03/2008"),
      indexInto("compositedatehistaggs") fields("name" -> "Walking Dead", "premiere_date" -> "19/01/2008")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "date histogram agg" - {

    "should return formatted keys grouped by histogram interval" in {

      val resp = client.execute {
        search("compositedatehistaggs").matchAllQuery().aggs {

          CompositeAggregation(
            name = "agg1",
            sources = Seq(
              DateHistogramValueSource(
                name = "dateHist",
                interval = DateHistogramInterval.Month.interval,
                field = Some("premiere_date"),
                script = None,
                order = Some("ASC"),
                timeZone = None,
                format = Some("dd.MM.yyyy"),
                missingBucket = true
              )
            )
          )
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.compositeAgg("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        CompositeAggBucket(Map("dateHist" -> "01.01.2008"), 3, Map.empty),
        CompositeAggBucket(Map("dateHist" -> "01.03.2008"), 1, Map.empty),
        CompositeAggBucket(Map("dateHist" -> "01.06.2008"), 2, Map.empty),
      )
    }


    "should return formatted keys grouped by histogram interval AFTER given key" in {

      val resp = client.execute {
        search("compositedatehistaggs").matchAllQuery().aggs {

          CompositeAggregation(
            name = "agg1",
            sources = Seq(
              DateHistogramValueSource(
                name = "dateHist",
                interval = DateHistogramInterval.Month.interval,
                field = Some("premiere_date"),
                script = None,
                order = Some("ASC"),
                timeZone = None,
                format = Some("dd.MM.yyyy")
              )
            ),
            after = Some(Map("dateHist" -> "01.03.2008"))
          )
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.compositeAgg("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        CompositeAggBucket(Map("dateHist" -> "01.06.2008"), 2, Map.empty),
      )
    }


  }
}
