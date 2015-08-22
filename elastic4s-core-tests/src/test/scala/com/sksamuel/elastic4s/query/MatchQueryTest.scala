package com.sksamuel.elastic4s
package query

import org.elasticsearch.index.query.MatchQueryBuilder
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.testkit.ElasticSugar

class MatchQueryTest extends WordSpec with MockitoSugar with ElasticSugar with ElasticDsl {

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
      assert(resp.getHits.totalHits === 1l)
    }
  }
}
