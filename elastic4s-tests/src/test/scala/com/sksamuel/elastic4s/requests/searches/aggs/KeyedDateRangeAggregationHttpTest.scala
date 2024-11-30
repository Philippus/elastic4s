package com.sksamuel.elastic4s.requests.searches.aggs

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.DateRangeBucket
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.{ElasticDate, ElasticDateMath}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class KeyedDateRangeAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("daterangeaggs")
    }.await
  }

  client.execute {
    createIndex("daterangeaggs").mapping(
      properties(
        textField("name").fielddata(true),
        dateField("premiere_date").format("dd/MM/yyyy")
      )
    )
  }.await

  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  client.execute(
    bulk(
      indexInto("daterangeaggs").fields(
        "name"          -> "Star Trek Picard",
        "premiere_date" -> LocalDate.of(2020, 1, 23).format(dateFormatter)
      ),
      indexInto("daterangeaggs").fields(
        "name"          -> "Better Call Saul",
        "premiere_date" -> LocalDate.of(2015, 2, 8).format(dateFormatter)
      ),
      indexInto("daterangeaggs").fields(
        "name"          -> "Star Trek Discovery",
        "premiere_date" -> LocalDate.of(2017, 9, 19).format(dateFormatter)
      ),
      indexInto("daterangeaggs").fields(
        "name"          -> "Game of Thrones",
        "premiere_date" -> LocalDate.of(2011, 4, 17).format(dateFormatter)
      ),
      indexInto("daterangeaggs").fields(
        "name"          -> "Designated Survivor",
        "premiere_date" -> LocalDate.of(2016, 9, 21).format(dateFormatter)
      ),
      indexInto("daterangeaggs").fields(
        "name"          -> "Walking Dead",
        "premiere_date" -> LocalDate.of(2011, 10, 31).format(dateFormatter)
      )
    ).refreshImmediately
  ).await

  "keyed date range agg" - {
    "should support elastic dates" in {

      val resp = client.execute {
        search("daterangeaggs").matchAllQuery().aggs {
          dateRangeAgg("agg1", "premiere_date")
            .range("old", ElasticDateMath("15/12/2010"), ElasticDate("15/12/2015"))
            .range("new", ElasticDateMath("15/12/2015"), ElasticDate("15/12/2020"))
            .keyed(true)
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.keyedDateRange("agg1")
      agg.buckets.mapValues(_.copy(data = Map.empty)).toMap shouldBe Map(
        "old" -> DateRangeBucket(
          Some("1.2923712E12"),
          Some("15/12/2010"),
          Some("1.4501376E12"),
          Some("15/12/2015"),
          None,
          3,
          Map.empty
        ),
        "new" -> DateRangeBucket(
          Some("1.4501376E12"),
          Some("15/12/2015"),
          Some("1.6079904E12"),
          Some("15/12/2020"),
          None,
          3,
          Map.empty
        )
      )
    }
    "should support string dates" in {

      val resp = client.execute {
        search("daterangeaggs").matchAllQuery().aggs {
          dateRangeAgg("agg1", "premiere_date")
            .range("old", "15/12/2010", "15/12/2015")
            .range("new", "15/12/2015", "15/12/2020")
            .keyed(true)
        }
      }.await.result

      resp.totalHits shouldBe 6

      val agg = resp.aggs.keyedDateRange("agg1")
      agg.buckets.mapValues(_.copy(data = Map.empty)).toMap shouldBe Map(
        "old" -> DateRangeBucket(
          Some("1.2923712E12"),
          Some("15/12/2010"),
          Some("1.4501376E12"),
          Some("15/12/2015"),
          None,
          3,
          Map.empty
        ),
        "new" -> DateRangeBucket(
          Some("1.4501376E12"),
          Some("15/12/2015"),
          Some("1.6079904E12"),
          Some("15/12/2020"),
          None,
          3,
          Map.empty
        )
      )
    }
  }
}
