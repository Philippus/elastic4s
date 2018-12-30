package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.DateRangeBucket
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.{ElasticDate, ElasticDateMath, Years}
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class DateRangeAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("daterangeaggs")
    }.await
  }

  client.execute {
    createIndex("daterangeaggs") mappings {
      mapping("tv") fields(
        textField("name").fielddata(true),
        dateField("premiere_date").format("dd/MM/yyyy")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("daterangeaggs/tv").fields("name" -> "Breaking Bad", "premiere_date" -> "20/01/2008"),
      indexInto("daterangeaggs/tv").fields("name" -> "Better Call Saul", "premiere_date" -> "15/01/2014"),
      indexInto("daterangeaggs/tv").fields("name" -> "Star Trek Discovery", "premiere_date" -> "27/06/2017"),
      indexInto("daterangeaggs/tv").fields("name" -> "Game of Thrones", "premiere_date" -> "01/06/2010"),
      indexInto("daterangeaggs/tv").fields("name" -> "Designated Survivor", "premiere_date" -> "12/03/2016"),
      indexInto("daterangeaggs/tv").fields("name" -> "Walking Dead", "premiere_date" -> "19/01/2011")
    ).refreshImmediately
  ).await

  "date range agg" - {
    "should support elastic dates" in {

      val resp = client.execute {
        search("daterangeaggs").matchAllQuery().aggs {
          dateRangeAgg("agg1", "premiere_date")
            .range(ElasticDateMath("15/12/2017").minus(10, Years), ElasticDate("15/12/2017").minus(5, Years))
            .range(ElasticDateMath("15/12/2017").minus(5, Years), ElasticDate("15/12/2017"))
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.dateRange("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        DateRangeBucket(Some("1.1976768E12"), Some("15/12/2007"), Some("1.3555296E12"), Some("15/12/2012"), Some("15/12/2007-15/12/2012"), 3, Map.empty),
        DateRangeBucket(Some("1.3555296E12"), Some("15/12/2012"), Some("1.513296E12"), Some("15/12/2017"), Some("15/12/2012-15/12/2017"), 3, Map.empty)
      )
    }
    "should support string dates" in {

      val resp = client.execute {
        search("daterangeaggs").matchAllQuery().aggs {
          dateRangeAgg("agg1", "premiere_date")
            .range("15/12/2017||-10y", "15/12/2017||-5y")
            .range("15/12/2017||-5y", "15/12/2017||")
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.dateRange("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        DateRangeBucket(Some("1.1976768E12"), Some("15/12/2007"), Some("1.3555296E12"), Some("15/12/2012"), Some("15/12/2007-15/12/2012"), 3, Map.empty),
        DateRangeBucket(Some("1.3555296E12"), Some("15/12/2012"), Some("1.513296E12"), Some("15/12/2017"), Some("15/12/2012-15/12/2017"), 3, Map.empty)
      )
    }
  }
}
