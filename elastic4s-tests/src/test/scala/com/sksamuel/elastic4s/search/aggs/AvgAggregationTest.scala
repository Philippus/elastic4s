package com.sksamuel.elastic4s.search.aggs

import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg

class AvgAggregationTest extends AbstractAggregationTest {

  "avg aggregation" - {
    "should average by field" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          avgAgg("agg1", "age")
        }
      }.await
      resp.totalHits shouldBe 10
      val agg = resp.aggregations.map("agg1").asInstanceOf[InternalAvg]
      agg.getValue shouldBe 45.4
    }
    "should only include matching documents in the query" in {
      val resp = client.execute {
        // should match 3 documents
        search("aggregations/breakingbad") query prefixQuery("name" -> "g") aggregations {
          avgAggregation("agg1").field("age")
        }
      }.await
      resp.totalHits shouldBe 3
      val agg = resp.aggregations.avgResult("agg1")
      agg.getValue shouldBe 55
    }
  }
}
