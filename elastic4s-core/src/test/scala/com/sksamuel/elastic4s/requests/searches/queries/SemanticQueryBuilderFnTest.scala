package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.api.QueryApi
import com.sksamuel.elastic4s.handlers.searches.queries.SemanticQueryBuilderFn
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SemanticQueryBuilderFnTest extends AnyFunSuite with QueryApi with Matchers with JsonSugar {
  test("Should correctly build semantic query") {
    val query = SemanticQuery("test", "my_query")

    val queryBody = SemanticQueryBuilderFn(query)

    queryBody.string shouldBe """{"semantic":{"field":"test","query":"my_query"}}"""
  }
}
