package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.aggs.responses.{AdjacencyMatrix, AdjacencyMatrixBucket}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class AdjacencyMatrixAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("adjacencymatrixsagg")
    }.await
  }

  client.execute {
    createIndex("adjacencymatrixsagg") mapping {
      mapping(
        keywordField("accounts")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("adjacencymatrixsagg").id("1").fields("accounts" -> Seq("hillary", "sidney")),
      indexInto("adjacencymatrixsagg").id("2").fields("accounts" -> Seq("hillary", "donald")),
      indexInto("adjacencymatrixsagg").id("3").fields("accounts" -> Seq("vladimir", "donald"))
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "adjacency matrix agg" - {
    "should create buckets matching the query" in {

      val resp                     = client.execute {
        search("adjacencymatrixsagg")
          .size(0)
          .aggs {
            adjacencyMatrixAgg(
              name = "interactions",
              filters = Seq(
                "grpA" -> termsQuery("accounts", Seq("hillary", "sidney")),
                "grpB" -> termsQuery("accounts", Seq("donald", "mitt")),
                "grpC" -> termsQuery("accounts", Seq("vladimir", "nigel"))
              )
            )
          }
      }.await.result
      resp.totalHits shouldBe 3
      val results: AdjacencyMatrix = resp.aggs.result[AdjacencyMatrix]("interactions")

      results.buckets should have size (5)

      results.buckets.map(_.copy(data = Map.empty)) should contain theSameElementsInOrderAs Seq(
        AdjacencyMatrixBucket("grpA", 2L, Map.empty),
        AdjacencyMatrixBucket("grpA&grpB", 1L, Map.empty),
        AdjacencyMatrixBucket("grpB", 2L, Map.empty),
        AdjacencyMatrixBucket("grpB&grpC", 1L, Map.empty),
        AdjacencyMatrixBucket("grpC", 1L, Map.empty)
      )
    }
  }
}
