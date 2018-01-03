package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, ElasticMatchers}
import com.sksamuel.elastic4s.{ElasticDateMath, Years}
import org.scalatest.WordSpec

import scala.util.Try

class DateRangeQueryHttpTest
  extends WordSpec
    with DiscoveryLocalNodeProvider
    with ElasticMatchers
    with ElasticDsl {

  Try {
    http.execute {
      ElasticDsl.deleteIndex("daterange")
    }.await
  }

  http.execute {
    ElasticDsl.createIndex("daterange") mappings {
      mapping("tv") fields(
        textField("name").fielddata(true),
        dateField("premiere_date").format("dd/MM/yyyy")
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("daterange/tv").fields("name" -> "Breaking Bad", "premiere_date" -> "20/01/2008"),
      indexInto("daterange/tv").fields("name" -> "Better Call Saul", "premiere_date" -> "15/01/2014"),
      indexInto("daterange/tv").fields("name" -> "Star Trek Discovery", "premiere_date" -> "27/06/2017"),
      indexInto("daterange/tv").fields("name" -> "Game of Thrones", "premiere_date" -> "01/06/2010"),
      indexInto("daterange/tv").fields("name" -> "Designated Survivor", "premiere_date" -> "12/03/2016"),
      indexInto("daterange/tv").fields("name" -> "Walking Dead", "premiere_date" -> "19/01/2011")
    ).refreshImmediately
  ).await

  "a range query" should {
    "support date math for gte" in {
      val resp = http.execute {
        search("daterange") query {
          rangeQuery("premiere_date").gte(ElasticDateMath("now").minus(5, Years))
        }
      }.await.right.get.result
      resp.totalHits shouldBe 3
    }
    "support date math for lte" in {
      val resp = http.execute {
        search("daterange") query {
          rangeQuery("premiere_date").lte(ElasticDateMath("now"))
        }
      }.await.right.get.result
      resp.totalHits shouldBe 6
    }
  }
}
