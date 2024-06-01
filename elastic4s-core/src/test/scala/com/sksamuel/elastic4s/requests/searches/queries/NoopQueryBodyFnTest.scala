package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class NoopQueryBodyFnTest extends AnyFunSuite with Matchers {
  test("NoopQuery should not generate a query body") {
    val q = boolQuery().should(Seq(NoopQuery))
    QueryBuilderFn(q).string shouldBe """{"bool":{"should":[]}}"""
  }
}
