package com.sksamuel.elastic4s.requests.searches

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class AdjacencyMatrixAggBuilderTest extends AnyFunSuite with Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  test("AdjacencyMatrixBuilder apply should return appropriate XContentBuilder") {
    val searchQuery = search("test")
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

    SearchBodyBuilderFn(searchQuery).string() shouldBe
      """{"size":0,"aggs":{"interactions":{"adjacency_matrix":{"filters":{"grpA":{"terms":{"accounts":["hillary","sidney"]}},"grpB":{"terms":{"accounts":["donald","mitt"]}},"grpC":{"terms":{"accounts":["vladimir","nigel"]}}}}}}}""".stripMargin
  }

}
