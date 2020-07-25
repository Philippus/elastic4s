package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.searches.queries.{FieldSortBuilderFn, RangeQuery}
import com.sksamuel.elastic4s.requests.searches.sort.FieldSort
import com.sksamuel.elastic4s.requests.searches.sort.SortMode.{Avg, Min}
import com.sksamuel.elastic4s.requests.searches.sort.SortOrder.Asc
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FieldSortBuilderFnTest extends AnyFunSuite with Matchers {

  test("field sort builder should support defining both nested path and nested filter") {
    val fieldSort = FieldSort(
      field = "parent.child.age",
      sortMode = Some(Min),
      order = Asc,
      nestedPath = Some("parent"),
      nestedFilter = Some(RangeQuery(field = "parent.child", gte = Some(21L)))
    )

    FieldSortBuilderFn(fieldSort).string() shouldBe
      """{"parent.child.age":{"mode":"min","order":"asc","nested":{"path":"parent","filter":{"range":{"parent.child":{"gte":21}}}}}}"""

  }
}
