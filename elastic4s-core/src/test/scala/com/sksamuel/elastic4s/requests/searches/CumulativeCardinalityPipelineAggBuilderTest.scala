package com.sksamuel.elastic4s.requests.searches

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CumulativeCardinalityPipelineAggBuilderTest extends AnyFunSuite with Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  test("cumulative cardinality agg should match the basic spec") {
    val search = SearchRequest("myIndex").aggs(
      dateHistogramAgg("users_per_day", "date")
        .fixedInterval(DateHistogramInterval.Month)
        .subaggs(
          Seq(
            cardinalityAgg("distinct_users", "user_id"),
            cumulativeCardinalityAggregation("total_new_users", "distinct_users")
          )
        )
    )
    SearchBodyBuilderFn(search).string shouldBe
      """{"aggs":{"users_per_day":{"date_histogram":{"fixed_interval":"1M","field":"date"},"aggs":{"distinct_users":{"cardinality":{"field":"user_id"}},"total_new_users":{"cumulative_cardinality":{"buckets_path":"distinct_users"}}}}}}"""
  }

}
