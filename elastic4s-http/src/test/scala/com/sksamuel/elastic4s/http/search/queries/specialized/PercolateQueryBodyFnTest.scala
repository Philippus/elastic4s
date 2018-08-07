package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.http.ElasticDsl._
import org.scalatest.{FunSuite, Matchers}

class PercolateQueryBodyFnTest extends FunSuite with Matchers {

  test("percolateQuery should generate expected json using id") {
    val q = percolateQuery("some_document_type", "some_field")
      .usingId("some_index", "some_type", "some_id")
    PercolateQueryBodyFn(q).string() shouldBe
      """{"percolate":{"field":"some_field","document_type":"some_document_type","index":"some_index","type":"some_type","id":"some_id"}}"""
  }

  test("percolateQuery should generate expected json using source") {
    val q = percolateQuery("some_document_type", "some_field")
      .usingSource("""{"message":"A new bonsai tree in the office"}""")
    PercolateQueryBodyFn(q).string() shouldBe
      """{"percolate":{"field":"some_field","document_type":"some_document_type","document":{"message":"A new bonsai tree in the office"}}}"""
  }
}
