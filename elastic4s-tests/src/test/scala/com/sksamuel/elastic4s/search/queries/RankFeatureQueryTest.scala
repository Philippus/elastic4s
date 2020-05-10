package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.fields.{RankFeatureField, RankFeaturesField}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class RankFeatureQueryTest extends AnyFlatSpec with Matchers with DockerTests {
  Try {
    client.execute {
      deleteIndex("rankfeaturetest")
    }.await
  }

  client.execute {
    createIndex("rankfeaturetest").mapping(
      properties(
        RankFeatureField("pagerank"),
        RankFeatureField("url_length", Some(false)),
        RankFeaturesField("topics")
      )
    )
  }.await

  client.execute {
    createIndex("rankfeaturetest")
  }.await

  client.execute {
    bulk(
      indexInto("rankfeaturetest").fields(
        "url" -> "http://en.wikipedia.org/wiki/2016_Summer_Olympics",
        "content" -> "Rio 2016",
        "pagerank" -> 50.3,
        "url_length" -> 42,
        "topics" -> Map("sports" -> 50, "brazil" -> 30)
        ),
      indexInto("rankfeaturetest").fields(
        "url" -> "http://en.wikipedia.org/wiki/2016_Brazilian_Grand_Prix",
        "content" -> "Formula One motor race held on 13 November 2016",
        "pagerank" -> 50.3,
        "url_length" -> 47,
        "topics" -> Map("sports" -> 35, "formula one" -> 65, "brazil" -> 20)
      ),
      indexInto("rankfeaturetest").fields(
        "url" -> "http://en.wikipedia.org/wiki/Deadpool_(film)",
        "content" -> "Deadpool is a 2016 American superhero film",
        "pagerank" -> 50.3,
        "url_length" -> 37,
        "topics" -> Map("movies" -> 60, "super hero" -> 65)
      )
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "rank feature query" should "work as in the elasticsearch docs" in {
    val resp = client.execute {
      search("rankfeaturetest").query {
        boolQuery()
          .must(matchQuery("content", "2016"))
          .should {
            List(rankFeatureQuery("pagerank"),
            rankFeatureQuery("url_length").boost(0.1),
            rankFeatureQuery("topics.sports").boost(0.4))
          }
      }
    }.await.result

    resp.totalHits shouldBe 3
    resp.hits.hits.head.sourceField("content") shouldBe "Rio 2016"
    resp.hits.hits.last.sourceField("content") shouldBe "Deadpool is a 2016 American superhero film"
  }
}
