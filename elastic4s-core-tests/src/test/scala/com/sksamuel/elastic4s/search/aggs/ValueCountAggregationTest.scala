package com.sksamuel.elastic4s.search.aggs

class ValueCountAggregationTest extends AbstractAggregationTest {

  "value count aggregation" - {
    "should sum values for field" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          valueCountAggregation("agg1") field "age"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.valueCountResult("agg1")
      aggs.getValue shouldBe 10
    }
  }
}
