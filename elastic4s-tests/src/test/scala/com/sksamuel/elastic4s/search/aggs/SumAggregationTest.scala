package com.sksamuel.elastic4s.search.aggs

class SumAggregationTest extends AbstractAggregationTest {

  "sum aggregation" - {
    "should sum values for field" in {

      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          sumAggregation("agg1").field("age")
        }
      }.await

      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.sumResult("agg1")
      aggs.getValue shouldBe 454.0
    }
  }
}
