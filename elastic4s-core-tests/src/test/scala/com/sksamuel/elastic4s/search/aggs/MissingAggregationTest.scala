package com.sksamuel.elastic4s.search.aggs

import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg

class MissingAggregationTest extends AbstractAggregationTest {

  "missing aggregation" - {
    "should return documents missing a value" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation missing "agg1" field "actor"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.missingResult("agg1")
      aggs.getDocCount shouldBe 7
    }
  }
}
