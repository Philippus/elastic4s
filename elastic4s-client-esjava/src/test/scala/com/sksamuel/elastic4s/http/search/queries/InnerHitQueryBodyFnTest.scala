package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.requests.searches.HighlightField
import com.sksamuel.elastic4s.requests.searches.queries.InnerHit
import com.sksamuel.elastic4s.requests.searches.queries.nested.InnerHitQueryBodyFn
import com.sksamuel.elastic4s.requests.searches.sort.FieldSort
import org.scalatest.{FunSuite, Matchers}

class InnerHitQueryBodyFnTest extends FunSuite with Matchers {

  test("inner hit should generate expected json") {
    val q = InnerHit("inners")
      .from(2)
      .explain(false)
      .trackScores(true)
      .version(true)
      .size(2)
      .docValueFields(List("df1", "df2"))
      .sortBy(FieldSort("sortField"))
      .storedFieldNames(List("field1", "field2"))
      .highlighting(HighlightField("hlField"))

    InnerHitQueryBodyFn(q).string() shouldBe
      """{"name":"inners","from":2,"explain":false,"track_scores":true,"version":true,"size":2,"docvalue_fields":["df1","df2"],"sort":[{"sortField":{"order":"asc"}}],"stored_fields":["field1","field2"],"highlight":{"fields":{"hlField":{}}}}"""
  }
}
