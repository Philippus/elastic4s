package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DockerTests, ElasticMatchers}
import com.sksamuel.elastic4s.{ElasticDateMath, Years}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.scalatest.WordSpec

import scala.util.Try

class DateRangeQueryHttpTest
    extends WordSpec
    with DockerTests
    with ElasticMatchers {

  Try {
    client.execute {
      ElasticDsl.deleteIndex("daterange")
    }.await
  }

  client.execute {
    ElasticDsl.createIndex("daterange") mappings {
      mapping("tv") fields (
        textField("name").fielddata(true),
        dateField("premiere_date").format("dd/MM/yyyy")
      )
    }
  }.await

  val dateFormatter: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy")

  client
    .execute(
      bulk(
        indexInto("daterange/tv").fields("name" -> "Breaking Bad",
                                         "premiere_date" -> DateTime
                                           .now()
                                           .minusYears(10)
                                           .toString(dateFormatter)),
        indexInto("daterange/tv").fields("name" -> "Better Call Saul",
                                         "premiere_date" -> DateTime
                                           .now()
                                           .minusYears(5)
                                           .minusMonths(1)
                                           .toString(dateFormatter)),
        indexInto("daterange/tv").fields("name" -> "Star Trek Discovery",
                                         "premiere_date" -> DateTime
                                           .now()
                                           .minusYears(2)
                                           .minusMonths(6)
                                           .toString(dateFormatter)),
        indexInto("daterange/tv").fields("name" -> "Game of Thrones",
                                         "premiere_date" -> DateTime
                                           .now()
                                           .minusYears(9)
                                           .minusMonths(6)
                                           .toString(dateFormatter)),
        indexInto("daterange/tv").fields("name" -> "Designated Survivor",
                                         "premiere_date" -> DateTime
                                           .now()
                                           .minusYears(3)
                                           .minusMonths(1)
                                           .toString(dateFormatter)),
        indexInto("daterange/tv").fields("name" -> "Walking Dead",
                                         "premiere_date" -> DateTime
                                           .now()
                                           .minusYears(8)
                                           .minusMonths(3)
                                           .toString(dateFormatter))
      ).refreshImmediately
    )
    .await

  "a range query" should {
    "support date math for gte" in {
      val resp = client
        .execute {
          search("daterange") query {
            rangeQuery("premiere_date").gte(
              ElasticDateMath("now").minus(5, Years))
          }
        }
        .await
        .result
      resp.totalHits shouldBe 2
    }
    "support date math for lte" in {
      val resp = client
        .execute {
          search("daterange") query {
            rangeQuery("premiere_date").lte(ElasticDateMath("now"))
          }
        }
        .await
        .result
      resp.totalHits shouldBe 6
    }
  }
}
