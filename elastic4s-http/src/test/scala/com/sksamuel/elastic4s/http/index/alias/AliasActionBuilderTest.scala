package com.sksamuel.elastic4s.http.index.alias

import com.sksamuel.elastic4s.alias.{
  AddAliasActionRequest,
  IndicesAliasesRequest,
  RemoveAliasAction
}
import com.sksamuel.elastic4s.searches.queries.matches.MatchAllQuery
import org.scalatest.{FunSuite, Matchers}

class AliasActionBuilderTest extends FunSuite with Matchers {

  test("alias action should generate expected json") {
    val actions = IndicesAliasesRequest(
      Seq(
        AddAliasActionRequest("alias1", "test"),
        RemoveAliasAction("alias1", "test1"),
        AddAliasActionRequest("alias2",
                              "test2",
                              filter = Some(MatchAllQuery()),
                              isWriteIndex = Some(true))
      ))

    AliasActionBuilder(actions).string() shouldBe
      """{"actions":[{"add":{"index":"test","alias":"alias1"}},{"remove":{"index":"test1","alias":"alias1"}},{"add":{"index":"test2","alias":"alias2","filter":{"match_all":{}},"is_write_index":true}}]}"""
  }

}
