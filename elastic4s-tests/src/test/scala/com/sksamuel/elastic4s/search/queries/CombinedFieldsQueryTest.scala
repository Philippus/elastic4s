package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.Operator
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.elastic4s.{ElasticDsl, Indexable}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class CombinedFieldsQueryTest extends AnyWordSpec with Matchers with DockerTests with ElasticDsl {

  case class Game(name: String, alternatives: String*)

  implicit object GameIndexable extends Indexable[Game] {

    def quote(value: String): String = s""""${value}""""

    override def json(t: Game): String =
      s""" { "name": "${t.name}", "alternatives": [ ${t.alternatives.map(quote).mkString(",")} ] } """
  }

  Try {
    client.execute {
      ElasticDsl.deleteIndex("boardgames")
    }.await
  }

  client.execute {
    bulk(
      indexInto("boardgames") source Game("Imperial Settlers"),
      indexInto("boardgames") source Game("Imperial Settlers - Empires of the North"),
      indexInto("boardgames") source Game("Catan", "Settlers of Catan"),
      indexInto("boardgames") source Game("Imperial"),
      indexInto("boardgames") source Game("Star Wars: Imperial Assault")
    ).refreshImmediately
  }.await

  "combined fields query" should {
    "perform query" in {
      val resp = client.execute {
        search("boardgames") query {
          combinedFieldsQuery("imperial settlers", Seq("name", "alternatives"))
        }
      }.await.result
      resp.totalHits shouldBe 5
    }
    "support and operator" in {
      val resp = client.execute {
        search("boardgames") query {
          combinedFieldsQuery("imperial settlers", Seq("name", "alternatives")).operator(Operator.AND)
        }
      }.await.result
      resp.totalHits shouldBe 2
    }
    "support minimumShouldMatch" in {
      val resp = client.execute {
        search("boardgames") query {
          combinedFieldsQuery("imperial wars of the north", Seq("name", "alternatives")).minimumShouldMatch("2")
        }
      }.await.result
      resp.totalHits shouldBe 2
    }
  }
}
