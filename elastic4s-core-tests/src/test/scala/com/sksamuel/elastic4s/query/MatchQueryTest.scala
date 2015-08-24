package com.sksamuel.elastic4s
package query

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.index.query.MatchQueryBuilder
import org.scalatest.{Matchers, WordSpec}

class MatchQueryTest extends WordSpec with Matchers with ElasticSugar  {

  import ElasticDsl._

  client.execute {
    bulk(
      index into "elite/ships" fields ("name" -> "vulture", "manufacturer" -> "Core Dynamics"),
      index into "elite/ships" fields ("name" -> "sidewinder", "manufacturer" -> "Core Dynamics"),
      index into "elite/ships" fields ("name" -> "cobra mark 3", "manufacturer" -> "Core Dynamics")
    )
  }.await

  blockUntilCount(3, "elite")

  "a match query" should {
    "accept _all field" in {
      val resp = client.execute {
        search in "elite" / "ships" query {
          matchQuery("_all", "vulture dynamics").operator(MatchQueryBuilder.Operator.AND)
        }
      }.await
      resp.totalHits shouldBe 1l
    }
  }
}
