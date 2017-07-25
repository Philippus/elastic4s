package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.CumulativeSumDefinition
import org.scalatest.{FunSuite, Matchers}

class CumulativeSumAggregationBuilderTest extends FunSuite with Matchers {

  test("cumulative sum aggregation should generate expected json") {
    val agg = CumulativeSumDefinition("myaggname", "myfieldname" )

    CumulativeSumAggregationBuilder(agg).string() shouldBe
      """{"cumulative_sum":{"buckets_path":"myfieldname"}}"""
  }

}
