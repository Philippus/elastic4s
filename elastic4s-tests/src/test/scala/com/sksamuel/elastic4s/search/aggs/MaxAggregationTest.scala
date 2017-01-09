package com.sksamuel.elastic4s.search.aggs

class MaxAggregationTest extends AbstractAggregationTest {

  "max aggregation" - {
    "should count max value for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          maxAggregation("agg1") field "age"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.maxResult("agg1")
      aggs.getValue shouldBe 60
    }
  }
}
