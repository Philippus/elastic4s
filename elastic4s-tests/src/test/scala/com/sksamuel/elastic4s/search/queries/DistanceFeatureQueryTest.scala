package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.fields.{DateField, GeoPointField, KeywordField}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class DistanceFeatureQueryTest extends AnyFlatSpec with Matchers with DockerTests {
  Try {
    client.execute {
      deleteIndex("distancefeaturetest")
    }.await
  }

  client.execute {
    createIndex("distancefeaturetest").mapping(
      properties(
        KeywordField("name"),
        DateField("production_date"),
        GeoPointField("location")
      )
    )
  }.await

  client.execute {
    createIndex("distancefeaturetest")
  }.await

  client.execute {
    bulk(
      indexInto("distancefeaturetest").fields(
        "name"            -> "chocolate",
        "production_date" -> "2018-02-01",
        "location"        -> List(71.34, 41.12)
      ),
      indexInto("distancefeaturetest").fields(
        "name"            -> "chocolate",
        "production_date" -> "2018-01-01",
        "location"        -> List(-71.3, 41.15)
      ),
      indexInto("distancefeaturetest").fields(
        "name"            -> "chocolate",
        "production_date" -> "2017-12-01",
        "location"        -> List(-71.3, 41.12)
      )
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "distance feature query" should "work as in the elasticsearch docs" in {
    val responseForDfqWithDate = client.execute {
      search("distancefeaturetest").query {
        boolQuery()
          .must(matchQuery("name", "chocolate"))
          .should {
            distanceFeatureQuery("production_date", "now", "7d")
          }
      }
    }.await.result

    responseForDfqWithDate.totalHits shouldBe 3
    responseForDfqWithDate.hits.hits.head.sourceField("production_date") shouldBe "2018-02-01"
    responseForDfqWithDate.hits.hits.last.sourceField("production_date") shouldBe "2017-12-01"

    val responseForDfqWithLocation = client.execute {
      search("distancefeaturetest").query {
        boolQuery()
          .must(matchQuery("name", "chocolate"))
          .should {
            distanceFeatureQuery("location", "41.15,-71.3", "1000m")
          }
      }
    }.await.result

    responseForDfqWithLocation.totalHits shouldBe 3
    responseForDfqWithLocation.hits.hits.head.sourceField("location") shouldBe List(-71.3, 41.15)
    responseForDfqWithLocation.hits.hits.last.sourceField("location") shouldBe List(71.34, 41.12)
  }
}
