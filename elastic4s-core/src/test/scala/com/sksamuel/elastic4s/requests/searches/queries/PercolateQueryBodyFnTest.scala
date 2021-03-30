package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class PercolateQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("percolateQuery should generate expected json using document ref") {
    val q = percolateQuery("some_document_type", "some_field")
      .usingId("some_index", "some_type", "some_id")
    QueryBuilderFn(q).string() shouldBe
      """{"percolate":{"field":"some_field","document_type":"some_document_type","index":"some_index","type":"some_type","id":"some_id"}}"""
  }

  test("percolateQuery should generate expected json using source") {
    val q = percolateQuery("some_document_type", "some_field")
      .usingSource("""{"message":"A new bonsai tree in the office"}""")
    QueryBuilderFn(q).string() shouldBe
      """{"percolate":{"field":"some_field","document_type":"some_document_type","document":{"message":"A new bonsai tree in the office"}}}"""
  }
}
