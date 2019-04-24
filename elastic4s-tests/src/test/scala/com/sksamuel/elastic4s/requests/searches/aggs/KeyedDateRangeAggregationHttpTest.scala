package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.DateRangeBucket
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.{ElasticDate, ElasticDateMath, Years}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class KeyedDateRangeAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("daterangeaggs")
    }.await
  }

  client.execute {
    createIndex("daterangeaggs") mappings {
      mapping() fields(
        textField("name").fielddata(true),
        dateField("premiere_date").format("dd/MM/yyyy")
      )
    }
  }.await

  val dateFormatter: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy")

  client.execute(
    bulk(
      indexInto("daterange").fields("name" -> "Breaking Bad",
        "premiere_date" -> DateTime
          .now()
          .minusYears(10)
          .toString(dateFormatter)),
      indexInto("daterange").fields("name" -> "Better Call Saul",
        "premiere_date" -> DateTime
          .now()
          .minusYears(5)
          .minusMonths(1)
          .toString(dateFormatter)),
      indexInto("daterange").fields("name" -> "Star Trek Discovery",
        "premiere_date" -> DateTime
          .now()
          .minusYears(2)
          .minusMonths(6)
          .toString(dateFormatter)),
      indexInto("daterange").fields("name" -> "Game of Thrones",
        "premiere_date" -> DateTime
          .now()
          .minusYears(9)
          .minusMonths(6)
          .toString(dateFormatter)),
      indexInto("daterange").fields("name" -> "Designated Survivor",
        "premiere_date" -> DateTime
          .now()
          .minusYears(3)
          .minusMonths(1)
          .toString(dateFormatter)),
      indexInto("daterange").fields("name" -> "Walking Dead",
        "premiere_date" -> DateTime
          .now()
          .minusYears(8)
          .minusMonths(3)
          .toString(dateFormatter))
    ).refreshImmediately
  ).await

  "keyed date range agg" - {
    "should support elastic dates" in {

      val resp = client.execute {
        search("daterangeaggs").matchAllQuery().aggs {
          dateRangeAgg("agg1", "premiere_date")
            .range("old", ElasticDateMath("15/12/2017").minus(10, Years), ElasticDate("15/12/2017").minus(5, Years))
            .range("new", ElasticDateMath("15/12/2017").minus(5, Years), ElasticDate("15/12/2017"))
            .keyed(true)
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.keyedDateRange("agg1")
      agg.buckets.mapValues(_.copy(data = Map.empty)) shouldBe Map(
        "old" -> DateRangeBucket(Some("1.1976768E12"), Some("15/12/2007"), Some("1.3555296E12"), Some("15/12/2012"), None, 3, Map.empty),
        "new" -> DateRangeBucket(Some("1.3555296E12"), Some("15/12/2012"), Some("1.513296E12"), Some("15/12/2017"), None, 3, Map.empty)
      )
    }
    "should support string dates" in {

      val resp = client.execute {
        search("daterangeaggs").matchAllQuery().aggs {
          dateRangeAgg("agg1", "premiere_date")
            .range("old", "15/12/2017||-10y", "15/12/2017||-5y")
            .range("new", "15/12/2017||-5y", "15/12/2017||")
            .keyed(true)
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.keyedDateRange("agg1")
      agg.buckets.mapValues(_.copy(data = Map.empty)) shouldBe Map(
        "old" -> DateRangeBucket(Some("1.1976768E12"), Some("15/12/2007"), Some("1.3555296E12"), Some("15/12/2012"), None, 3, Map.empty),
        "new" -> DateRangeBucket(Some("1.3555296E12"), Some("15/12/2012"), Some("1.513296E12"), Some("15/12/2017"), None, 3, Map.empty)
      )
    }
  }
}
