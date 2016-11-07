package com.sksamuel.elastic4s.search.aggs

class CardinalityAggregationTest extends AbstractAggregationTest {

  "cardinality aggregation" - {
    "should count distinct values" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation cardinality "agg1" field "job"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.cardinalityResult("agg1")
      aggs.getValue shouldBe 5
    }
  }
}
